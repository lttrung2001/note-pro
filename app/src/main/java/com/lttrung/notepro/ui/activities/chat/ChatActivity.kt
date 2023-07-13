package com.lttrung.notepro.ui.activities.chat

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.graphics.Bitmap
import android.os.Bundle
import android.os.IBinder
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityChatBinding
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.data.networks.models.User
import com.lttrung.notepro.ui.adapters.MessageAdapter
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.ui.dialogs.builders.DialogBuilder
import com.lttrung.notepro.ui.fragments.BottomSheetGallery
import com.lttrung.notepro.ui.fragments.PlayVideoFragment
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_CHANNEL_ID
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE_RECEIVED
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.PAGE_LIMIT
import com.lttrung.notepro.utils.JitsiHelper
import com.lttrung.notepro.utils.MediaType
import com.lttrung.notepro.utils.NotificationHelper
import com.lttrung.notepro.utils.openCamera
import com.lttrung.notepro.utils.requestPermissionToOpenCamera
import com.lttrung.notepro.utils.toByteArray
import dagger.hilt.android.AndroidEntryPoint
import org.jitsi.meet.sdk.JitsiMeetActivity
import java.io.ByteArrayOutputStream
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ChatActivity : BaseActivity() {
    @Inject
    lateinit var storageRef: StorageReference
    private lateinit var socketService: ChatSocketService
    override val binding by lazy {
        ActivityChatBinding.inflate(layoutInflater)
    }
    override val viewModel: ChatViewModel by viewModels()
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // Camera result
                handleCameraResult(result)
            }
        }

    private val messageAdapter by lazy {
        MessageAdapter().setVideoOnClick {
            PlayVideoFragment(it).apply {
                show(supportFragmentManager, tag)
            }
        }
    }
    private val note by lazy {
        intent.getSerializableExtra(NOTE) as Note
    }
    private val messageReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val message = intent?.getSerializableExtra(MESSAGE) as Message
                handleIncomingMessage(message)
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
        viewModel.messagesLiveData.observe(this) { preMessages ->
            viewModel.isLoading.postValue(false)
            messageAdapter.submitList(viewModel.listMessage)
            if (preMessages.isEmpty()) {
                binding.messages.removeOnScrollListener(onScrollListener)
            }
        }
    }

    private fun observeUploadResultData() {
        viewModel.uploadLiveData.observe(this@ChatActivity) { map ->
            val message = Message(
                System.currentTimeMillis().toString(),
                map["URL"] ?: "",
                map["TYPE"] ?: "",
                note.id,
                0L,
                User(messageAdapter.userId, "")
            )
            viewModel.listMessage.add(message)
            messageAdapter.submitList(viewModel.listMessage)
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
            btnChooseImage.setOnClickListener {
                BottomSheetGallery(MediaType.IMAGE).apply {
                    show(supportFragmentManager, tag)
                }
            }
            btnChooseVideo.setOnClickListener {
                BottomSheetGallery(MediaType.VIDEO).apply {
                    show(supportFragmentManager, tag)
                }
            }
            btnCall.setOnClickListener {
                viewModel.currentUserLiveData.value?.let { currentUser ->
                    socketService.call(note.id)
                    val options = JitsiHelper.createOptions(note.id, currentUser)
                    JitsiMeetActivity.launch(this@ChatActivity, options)
                    return@setOnClickListener
                }
                DialogBuilder(this@ChatActivity)
                    .setNotice(R.string.error_can_not_make_call)
                    .setCanTouchOutside(false)
                    .build()
                    .show()
            }
            btnInfo.setOnClickListener {
                // Start info activity
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
            Snackbar.make(
                this@ChatActivity,
                binding.root,
                getString(R.string.message_content_empty_notice),
                Snackbar.LENGTH_LONG
            ).show()
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

        viewModel.listMessage.add(message)
        messageAdapter.submitList(viewModel.listMessage)
        binding.messageBox.setText("")
    }

    private fun handleIncomingMessage(message: Message) {
        if (message.room == note.id) {
            viewModel.listMessage.add(message)
            messageAdapter.submitList(viewModel.listMessage)
        } else {
            NotificationHelper.pushNotification(
                this@ChatActivity, CHAT_CHANNEL_ID, message
            )
        }
    }

    private fun handleCameraResult(result: ActivityResult) {
        val image = result.data?.extras?.get("data") as Bitmap
        storageRef.child("images/messages/${System.currentTimeMillis()}.jpg")
            .putBytes(image.toByteArray()).addOnSuccessListener { task ->
                task.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                    viewModel.saveUploadResult(hashMapOf<String, String>().apply {
                        put("TYPE", AppConstant.MESSAGE_CONTENT_TYPE_IMAGE)
                        put("URL", uri.toString())
                    })
                }
            }
    }
}