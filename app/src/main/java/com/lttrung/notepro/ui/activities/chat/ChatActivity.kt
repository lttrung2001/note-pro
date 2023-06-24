package com.lttrung.notepro.ui.activities.chat

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.lttrung.notepro.databinding.ActivityChatBinding
import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.data.networks.models.User
import com.lttrung.notepro.ui.adapters.MessageAdapter
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_CHANNEL_ID
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE_RECEIVED
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.PAGE_LIMIT
import com.lttrung.notepro.utils.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : BaseActivity() {
    private lateinit var socketService: ChatSocketService
    private lateinit var currentUser: CurrentUser
    override val binding by lazy {
        ActivityChatBinding.inflate(layoutInflater)
    }
    override val viewModel: ChatViewModel by viewModels()
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
                if (message.room == note.id) {
                    val messages = viewModel.messagesLiveData.value.orEmpty().toMutableList()
                    messages.add(message)
                    // Update adapter
                    messageAdapter.submitList(messages)
                    // Scroll to new message
                    binding.messages.smoothScrollToPosition(messages.size - 1)
                    // Update live data
                    viewModel.messagesLiveData.postValue(messages)
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

    private val onScrollListener by lazy {
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (viewModel.isLoading.value == false) {
                    if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        // Load message here...
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
            }

            override fun onServiceDisconnected(p0: ComponentName?) {

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getCurrentUser()
    }

    override fun onStart() {
        super.onStart()

        registerReceivers()
        bindService()
    }

    override fun onStop() {
        super.onStop()

        unregisterReceiver(messageReceiver)
        unbindService(connection)
    }

    private fun bindService() {
        Intent(this, ChatSocketService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun registerReceivers() {
        val messageReceivedIntentFilter = IntentFilter(MESSAGE_RECEIVED)
        registerReceiver(messageReceiver, messageReceivedIntentFilter)
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.currentUserLiveData.observe(this) { user ->
            currentUser = user
            currentUser.id ?: finish()
            messageAdapter.userId = currentUser.id!!
            viewModel.getMessages(note.id, viewModel.page, PAGE_LIMIT)
        }
        viewModel.messagesLiveData.observe(this) { messages ->
            messageAdapter.submitList(messages)
        }
    }

    override fun initListeners() {
        super.initListeners()
        binding.messages.addOnScrollListener(onScrollListener)
        binding.sendMessageButton.setOnClickListener {
            sendMessage()
        }
    }

    override fun initViews() {
        super.initViews()
        binding.messages.adapter = messageAdapter
    }

    private fun sendMessage() {
        val content = binding.messageBox.text?.trim().toString()

        if (content.isBlank()) {
            return
        }

        val uid = currentUser.id ?: ""

        val message = Message(
            System.currentTimeMillis().toString(),
            content,
            note.id,
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