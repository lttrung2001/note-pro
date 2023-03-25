package com.lttrung.notepro.ui.chat

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.lttrung.notepro.database.data.locals.entities.Message
import com.lttrung.notepro.database.data.locals.entities.Note
import com.lttrung.notepro.databinding.ActivityChatBinding
import com.lttrung.notepro.services.ChatSocketService
import com.lttrung.notepro.ui.base.adapters.message.MessageAdapter
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_CHANNEL_ID
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private val chatViewModel: ChatViewModel by viewModels()
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var socketService: ChatSocketService

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ChatSocketService.LocalBinder
            socketService = binder.getService()
            initObservers()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            chatViewModel.userLiveData.removeObservers(this@ChatActivity)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initListeners()
        initAdapters()
    }

    private fun initObservers() {
        socketService.messageLiveData.observe(this) { message ->
            val note = intent.getSerializableExtra(NOTE) as Note
            val room = note.id
            if (message.room == room) {
                val messages = messageAdapter.currentList.toMutableList()
                messages.add(message)
                messageAdapter.submitList(messages)
                binding.messages.smoothScrollToPosition(messages.size - 1)
            } else {
                NotificationHelper.pushNotification(
                    applicationContext,
                    CHAT_CHANNEL_ID,
                    message.room,
                    "From ${message.userName}: ${message.content}"
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, ChatSocketService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        socketService.messageLiveData.removeObservers(this)
        unbindService(connection)
    }

    private fun initListeners() {
        binding.sendMessageButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun initAdapters() {
        lifecycleScope.launch(Dispatchers.IO) {
            val userId = chatViewModel.userLiveData.value?.id ?: ""
            messageAdapter = MessageAdapter(userId)
            val linearLayoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
            }
            val data = mutableListOf<Message>()
            binding.messages.apply {
                adapter = messageAdapter
                layoutManager = linearLayoutManager
            }
            messageAdapter.submitList(data)
        }
    }

    private fun initViews() {
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return true
    }

    private fun sendMessage() {
        val user = chatViewModel.userLiveData.value
        val content = binding.messageBox.text?.trim().toString()
        val room = (intent.getSerializableExtra(NOTE) as Note).id

        val message = Message(
            System.currentTimeMillis().toString(),
            user?.id ?: "",
            user?.fullName ?: "",
            content,
            room,
            0L
        )
        socketService.sendMessage(message)

        val messages = messageAdapter.currentList.toMutableList()
        messages.add(message)
        messageAdapter.submitList(messages)
        binding.messageBox.setText("")
        binding.messages.smoothScrollToPosition(messages.size - 1)
    }
}