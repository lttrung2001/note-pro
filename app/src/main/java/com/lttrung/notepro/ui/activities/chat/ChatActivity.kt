package com.lttrung.notepro.ui.activities.chat

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.databinding.ActivityChatBinding
import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.data.networks.models.User
import com.lttrung.notepro.ui.adapters.MessageAdapter
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.ui.fragments.BottomSheetGallery
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_CHANNEL_ID
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE_RECEIVED
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.PAGE_LIMIT
import com.lttrung.notepro.utils.NotificationHelper
import com.lttrung.notepro.utils.openCamera
import com.lttrung.notepro.utils.requestPermissionToOpenCamera
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : BaseActivity() {
    private lateinit var socketService: ChatSocketService
    override val binding by lazy {
        ActivityChatBinding.inflate(layoutInflater)
    }
    override val viewModel: ChatViewModel by viewModels()
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // If camera
                // If choose from gallery
            }
        }
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
                    val messages = messageAdapter.currentList.toMutableList().apply {
                        add(message)
                    }
                    // Update live data
                    viewModel.messagesLiveData.postValue(messages)
                } else {
                    NotificationHelper.pushNotification(
                        this@ChatActivity, CHAT_CHANNEL_ID, message
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
                        viewModel.isLoading.postValue(true)
                        viewModel.getMessages(note.id, viewModel.page, PAGE_LIMIT)
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
    private var bottomGallery: BottomSheetGallery? = null

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
        observeCurrentUserData()
        observeGetMessagesData()
        observeUploadResultData()
    }

    private fun observeGetMessagesData() {
        viewModel.messagesLiveData.observe(this) { messages ->
            viewModel.isLoading.postValue(false)
            messageAdapter.submitList(messages)
            if (messages.isEmpty()) {
                binding.messages.removeOnScrollListener(onScrollListener)
            }
        }
    }

    private fun observeUploadResultData() {
        viewModel.uploadLiveData.observe(this@ChatActivity) { uri ->
            val message = Message(
                System.currentTimeMillis().toString(),
                uri,
                AppConstant.MESSAGE_CONTENT_TYPE_IMAGE,
                note.id,
                0L,
                User(messageAdapter.userId, "")
            )
            viewModel.messagesLiveData.postValue(messageAdapter.currentList.toMutableList().apply {
                add(message)
            })
            socketService.sendMessage(message)
        }
    }

    private fun observeCurrentUserData() {
        viewModel.currentUserLiveData.observe(this) { user ->
            user.id ?: finish()
            messageAdapter.userId = user.id!!
            viewModel.getMessages(note.id, viewModel.page, PAGE_LIMIT)
        }
    }

    override fun initListeners() {
        super.initListeners()
        binding.apply {
            messages.addOnScrollListener(onScrollListener)
            sendMessageButton.setOnClickListener {
                sendMessage()
            }
            btnOpenCamera.setOnClickListener {
                if (requestPermissionToOpenCamera(this@ChatActivity)) {
                    openCamera(launcher)
                }
            }
            btnBottomSheetGallery.setOnClickListener {
                bottomGallery = BottomSheetGallery()
                bottomGallery?.show(supportFragmentManager, bottomGallery?.tag)
            }
        }
    }

    override fun initViews() {
        super.initViews()
        initMessageRecyclerView()
    }

    private fun initMessageRecyclerView() {
        binding.messages.apply {
            adapter = messageAdapter
            setItemViewCacheSize(100)
        }
    }

    private fun sendMessage() {
        val content = binding.messageBox.text?.trim().toString()

        if (content.isBlank()) {
            return
        }

        val uid = messageAdapter.userId

        val message = Message(
            System.currentTimeMillis().toString(),
            content,
            AppConstant.MESSAGE_CONTENT_TYPE_TEXT,
            note.id,
            0L,
            User(uid, "")
        )
        socketService.sendMessage(message)

        val messages = messageAdapter.currentList.toMutableList().apply {
            add(message)
        }
        viewModel.messagesLiveData.postValue(messages)
        binding.messageBox.setText("")
    }
}