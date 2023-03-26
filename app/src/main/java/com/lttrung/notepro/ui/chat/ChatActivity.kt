package com.lttrung.notepro.ui.chat

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.lttrung.notepro.NoteProApplication
import com.lttrung.notepro.database.data.locals.entities.Message
import com.lttrung.notepro.database.data.locals.entities.User
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.databinding.ActivityChatBinding
import com.lttrung.notepro.services.ChatSocketService
import com.lttrung.notepro.ui.base.adapters.message.MessageAdapter
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_CHANNEL_ID
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.ROOM_ID
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
            val room = intent.getStringExtra(ROOM_ID)
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
                    "From ${message.user.fullName}: ${message.content}",
                    message
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        (application as NoteProApplication).isChatActivity = true
        Intent(this, ChatSocketService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        (application as NoteProApplication).isChatActivity = false
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
            binding.messages.apply {
                adapter = messageAdapter
                layoutManager = linearLayoutManager
            }
            val messageFromNotification = intent.getSerializableExtra(MESSAGE) as Message?
            if (messageFromNotification == null) {
                Log.e("ERROR", "NULL")
            }
            messageFromNotification?.let {
                Log.i("INFO", it.toString())
                val messages = messageAdapter.currentList.toMutableList()
                messages.add(it)
                messageAdapter.submitList(messages)
            }
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
        val user = chatViewModel.userLiveData.value!!
        val content = binding.messageBox.text?.trim().toString()
        val room = intent.getSerializableExtra(ROOM_ID) as String

        val message = Message(
            System.currentTimeMillis().toString(),
            content,
            room,
            0L,
            User(user.id ?: "", user.fullName ?: "")
        )
        socketService.sendMessage(message)

        val messages = messageAdapter.currentList.toMutableList()
        messages.add(message)
        messageAdapter.submitList(messages)
        binding.messageBox.setText("")
        binding.messages.smoothScrollToPosition(messages.size - 1)
    }
}