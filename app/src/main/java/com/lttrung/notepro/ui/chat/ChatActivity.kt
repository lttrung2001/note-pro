package com.lttrung.notepro.ui.chat

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.lttrung.notepro.NoteProApplication
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Message
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.database.data.networks.models.User
import com.lttrung.notepro.databinding.ActivityChatBinding
import com.lttrung.notepro.services.ChatSocketService
import com.lttrung.notepro.ui.base.adapters.message.MessageAdapter
import com.lttrung.notepro.ui.showmembers.ShowMembersActivity
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_CHANNEL_ID
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE_RECEIVED
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.ROOM_ID
import com.lttrung.notepro.utils.NotificationHelper
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
                val room = this@ChatActivity.intent.getStringExtra(ROOM_ID)
                if (message.room == room) {
                    val messages = messageAdapter.currentList.toMutableList()
                    messages.add(message)
                    messageAdapter.submitList(messages)
                    binding.messages.smoothScrollToPosition(messages.size - 1)
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

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ChatSocketService.LocalBinder
            socketService = binder.getService()
            val roomId = intent.getStringExtra(ROOM_ID)!!
            socketService.getMessages(roomId)
            socketService.socket.on("load_messages") { args ->
                runOnUiThread {
                    val messages =
                        Gson().fromJson(args[0].toString(), Array<Message>::class.java).toList()
                    messageAdapter.submitList(messages)
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initListeners()
        initAdapters()
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(MESSAGE_RECEIVED)
        registerReceiver(messageReceiver, intentFilter)

        (application as NoteProApplication).chatActivity = this
        Intent(this, ChatSocketService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(messageReceiver)

        (application as NoteProApplication).chatActivity = null
        unbindService(connection)
    }

    private fun initListeners() {
        binding.sendMessageButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun initAdapters() {
        val userId = chatViewModel.userIdLiveData.value!!

        messageAdapter = MessageAdapter(userId)
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
        val uid = chatViewModel.userIdLiveData.value!!
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