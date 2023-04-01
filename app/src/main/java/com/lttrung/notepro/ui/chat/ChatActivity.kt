package com.lttrung.notepro.ui.chat

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.lttrung.notepro.NoteProApplication
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Message
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.database.data.networks.models.User
import com.lttrung.notepro.databinding.ActivityChatBinding
import com.lttrung.notepro.ui.base.adapters.message.MessageAdapter
import com.lttrung.notepro.ui.showmembers.ShowMembersActivity
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_CHANNEL_ID
import com.lttrung.notepro.utils.AppConstant.Companion.LOAD_MESSAGES_RECEIVED
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGES_JSON
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE_RECEIVED
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.PAGE_LIMIT
import com.lttrung.notepro.utils.AppConstant.Companion.ROOM_ID
import com.lttrung.notepro.utils.NotificationHelper
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var socketService: ChatSocketService
    private val messageReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val message = intent?.getSerializableExtra(MESSAGE) as Message
                Log.i("INFO", message.toString())
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

    private val loadMessagesReceiver: BroadcastReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                chatViewModel.page += 1
                val messagesJson = intent?.getStringExtra(MESSAGES_JSON)
                val olderMessages =
                    Gson().fromJson(messagesJson, Array<Message>::class.java).toList()
                val messages = messageAdapter.currentList.toMutableList()
                messages.addAll(0, olderMessages)
                // Update live data
                chatViewModel.messagesLiveData.postValue(Resource.Success(messages))
            }
        }
    }

    private val onScrollListener: RecyclerView.OnScrollListener by lazy {
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val note = intent.getSerializableExtra(NOTE) as Note
                    socketService.getMessages(
                        note.id,
                        chatViewModel.page,
                        PAGE_LIMIT
                    )
                    chatViewModel.messagesLiveData.postValue(Resource.Loading())
                }
            }
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ChatSocketService.LocalBinder
            socketService = binder.getService()
            val roomId = intent.getStringExtra(ROOM_ID)!!
            socketService.getMessages(
                roomId,
                chatViewModel.page,
                PAGE_LIMIT
            )
            chatViewModel.messagesLiveData.postValue(Resource.Loading())
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initListeners()
        initAdapters()
        initObservers()
        chatViewModel.getCurrentUser()
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
                    binding.sendMessageButton.isClickable = false
                }
                is Resource.Success -> {
                    binding.sendMessageButton.isClickable = true
                    messageAdapter.submitList(resource.data)
//                    binding.messages.smoothScrollToPosition(messageAdapter.itemCount - 1)
                }
                is Resource.Error -> {
                    binding.sendMessageButton.isClickable = true
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val messageReceivedIntentFilter = IntentFilter(MESSAGE_RECEIVED)
        registerReceiver(messageReceiver, messageReceivedIntentFilter)
        val loadMessagesIntentFilter = IntentFilter(LOAD_MESSAGES_RECEIVED)
        registerReceiver(loadMessagesReceiver, loadMessagesIntentFilter)

        (application as NoteProApplication).chatActivity = this
        Intent(this, ChatSocketService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()

        unregisterReceiver(messageReceiver)
        unregisterReceiver(loadMessagesReceiver)

        (application as NoteProApplication).chatActivity = null
        unbindService(connection)
    }

    private fun initListeners() {
        binding.sendMessageButton.setOnClickListener {
            sendMessage()
        }
        binding.messages.addOnScrollListener(onScrollListener)
    }

    private fun initAdapters() {
        messageAdapter = MessageAdapter()
        val linearLayoutManager = LinearLayoutManager(this@ChatActivity).apply {
            stackFromEnd = true
        }
        binding.messages.apply {
            adapter = messageAdapter
            layoutManager = linearLayoutManager
        }
    }

    private fun initViews() {
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_chat, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_show_members -> {
                val note = intent.getSerializableExtra(NOTE) as Note
                val viewMembersIntent =
                    Intent(this@ChatActivity, ShowMembersActivity::class.java).apply {
                        putExtra(NOTE, note)
                    }
                startActivity(viewMembersIntent)
            }
            else -> {
                finish()
            }
        }
        return true
    }

    private fun sendMessage() {
        val uid = messageAdapter.userId
        val content = binding.messageBox.text?.trim().toString()
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