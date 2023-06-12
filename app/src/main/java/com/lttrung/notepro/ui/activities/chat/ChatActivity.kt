package com.lttrung.notepro.ui.activities.chat

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityChatBinding
import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.data.networks.models.User
import com.lttrung.notepro.ui.base.adapters.message.MessageAdapter
import com.lttrung.notepro.ui.viewmembers.ViewMembersActivity
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_CHANNEL_ID
import com.lttrung.notepro.utils.AppConstant.Companion.LOAD_MESSAGES_RECEIVED
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGES_JSON
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE_RECEIVED
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.PAGE_LIMIT
import com.lttrung.notepro.utils.AppConstant.Companion.ROOM_ID
import com.lttrung.notepro.utils.AppConstant.Companion.USER
import com.lttrung.notepro.utils.JitsiHelper
import com.lttrung.notepro.utils.NotificationHelper
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import org.jitsi.meet.sdk.JitsiMeetActivity

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {
    private lateinit var socketService: ChatSocketService
    private val binding by lazy {
        ActivityChatBinding.inflate(layoutInflater)
    }
    private val chatViewModel: ChatViewModel by viewModels()
    private val messageAdapter by lazy {
        MessageAdapter()
    }
    private val note by lazy {
        intent.getSerializableExtra(NOTE) as Note
    }
    private val messageReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val message = intent?.getSerializableExtra(MESSAGE) as Message
                val room = this@ChatActivity.intent.getStringExtra(ROOM_ID)
                if (message.room == room) {
                    val messages = messageAdapter.currentList.toMutableList()
                    messages.add(message)
                    // Update adapter
                    messageAdapter.submitList(messages)
                    // Scroll to new message
                    binding.messages.smoothScrollToPosition(messages.size - 1)
                    // Update live data
                    chatViewModel.messagesLiveData.postValue(Resource.Success(messages))
                } else {
                    NotificationHelper.pushNotification(
                        this@ChatActivity,
                        CHAT_CHANNEL_ID,
                        message
                    )
                }
            }
        }
    }

    private val loadMessagesReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val messagesJson = intent?.getStringExtra(MESSAGES_JSON)
                // Parse JsonArray to Array
                val olderMessages =
                    Gson().fromJson(messagesJson, Array<Message>::class.java).toList()
                // Update scroll listener
                if (olderMessages.isEmpty()) {
                    binding.messages.removeOnScrollListener(onScrollListener)
                } else {
                    binding.messages.addOnScrollListener(onScrollListener)
                }
                val messages = messageAdapter.currentList.toMutableList()
                messages.addAll(0, olderMessages)
                // Update live data
                chatViewModel.messagesLiveData.postValue(Resource.Success(messages))
                chatViewModel.page += 1
            }
        }
    }

    private val onScrollListener by lazy {
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (chatViewModel.messagesLiveData.value !is Resource.Loading) {
                    if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        chatViewModel.messagesLiveData.postValue(Resource.Loading())
                        socketService.getMessages(
                            note.id,
                            chatViewModel.page,
                            PAGE_LIMIT
                        )
                    }
                }
            }
        }
    }

    private val connection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as ChatSocketService.LocalBinder
                socketService = binder.getService()
                chatViewModel.messagesLiveData.postValue(Resource.Loading())
                val roomId = intent.getStringExtra(ROOM_ID)!!
                socketService.getMessages(
                    roomId,
                    chatViewModel.page,
                    PAGE_LIMIT
                )
            }

            override fun onServiceDisconnected(p0: ComponentName?) {

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initListeners()
        initObservers()
        chatViewModel.getCurrentUser()
    }

    override fun onStart() {
        super.onStart()

        registerReceivers()
        bindService()
    }

    override fun onStop() {
        super.onStop()

        removeLoadingIfNeeded()

        unregisterReceiver(messageReceiver)
        unregisterReceiver(loadMessagesReceiver)

        unbindService(connection)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_show_members -> {
                val viewMembersIntent =
                    Intent(this@ChatActivity, ViewMembersActivity::class.java).apply {
                        putExtra(NOTE, note)
                    }
                startActivity(viewMembersIntent)
            }

            R.id.action_call -> {
                val currentUser = intent.getSerializableExtra(USER) as CurrentUser?
                if (currentUser != null) {
                    val roomId = note.id
                    socketService.call(roomId)
                    val options = JitsiHelper.createOptions(roomId, currentUser)
                    JitsiMeetActivity.launch(this, options)
                }
            }

            else -> {
                finish()
            }
        }
        return true
    }

    private fun removeLoadingIfNeeded() {
        if (chatViewModel.messagesLiveData.value is Resource.Loading) {
            messageAdapter.removeLoadingElement()
        }
    }

    private fun bindService() {
        Intent(this, ChatSocketService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun registerReceivers() {
        val messageReceivedIntentFilter = IntentFilter(MESSAGE_RECEIVED)
        registerReceiver(messageReceiver, messageReceivedIntentFilter)
        val loadMessagesIntentFilter = IntentFilter(LOAD_MESSAGES_RECEIVED)
        registerReceiver(loadMessagesReceiver, loadMessagesIntentFilter)
    }

    private fun initObservers() {
        chatViewModel.currentUserLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.sendMessageButton.isClickable = false
                }

                is Resource.Success -> {
                    binding.sendMessageButton.isClickable = true
                    val user = resource.data
                    intent.putExtra(USER, user)
                    user.id?.let { messageAdapter.userId = it }
                }

                is Resource.Error -> {
                    binding.sendMessageButton.isClickable = true
                }
            }
        }
        chatViewModel.messagesLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.messages.removeOnScrollListener(onScrollListener)
                    messageAdapter.showLoading()
                    binding.messages.smoothScrollToPosition(0)
                    binding.sendMessageButton.isClickable = false
                }

                is Resource.Success -> {
                    messageAdapter.hideLoading(resource.data.toMutableList())
                    binding.sendMessageButton.isClickable = true
                }

                is Resource.Error -> {
                    messageAdapter.removeLoadingElement()
                    binding.sendMessageButton.isClickable = true
                }
            }
        }
    }

    private fun initListeners() {
        binding.sendMessageButton.setOnClickListener {
            sendMessage()
        }

    }

    private fun initViews() {
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.messages.adapter = messageAdapter
    }

    private fun sendMessage() {
        val content = binding.messageBox.text?.trim().toString()

        if (content.isBlank()) {
            return
        }

        val uid = messageAdapter.userId
        val room = intent.getStringExtra(ROOM_ID)!!

        val message = Message(
            System.currentTimeMillis().toString(),
            content,
            room,
            0L,
            User(uid, "")
        )
        socketService.sendMessage(message)

        val messages = messageAdapter.currentList.toMutableList()
        messages.add(message)
        messageAdapter.submitList(messages)
        binding.messageBox.setText("")
        binding.messages.smoothScrollToPosition(messages.size - 1)
    }
}