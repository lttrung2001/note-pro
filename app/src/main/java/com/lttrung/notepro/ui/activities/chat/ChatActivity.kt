package com.lttrung.notepro.ui.activities.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.storage.StorageReference
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityChatBinding
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.data.networks.models.Theme
import com.lttrung.notepro.domain.data.networks.models.User
import com.lttrung.notepro.ui.adapters.MessageAdapter
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.ui.dialogs.builders.DialogBuilder
import com.lttrung.notepro.ui.fragments.BottomSheetGallery
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.CHANGE_THEME_RECEIVED
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_CHANNEL_ID
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE_RECEIVED
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.PAGE_LIMIT
import com.lttrung.notepro.utils.AppConstant.Companion.THEME
import com.lttrung.notepro.utils.JitsiHelper
import com.lttrung.notepro.utils.MediaType
import com.lttrung.notepro.utils.NotificationHelper
import com.lttrung.notepro.utils.openCamera
import com.lttrung.notepro.utils.requestPermissionToOpenCamera
import com.lttrung.notepro.utils.requestPermissionToReadGallery
import com.lttrung.notepro.utils.toByteArray
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import org.jitsi.meet.sdk.JitsiMeetActivity
import javax.inject.Inject


@AndroidEntryPoint
class ChatActivity : BaseActivity() {
    @Inject
    lateinit var storageRef: StorageReference
    override val binding by lazy {
        ActivityChatBinding.inflate(layoutInflater)
    }
    override val viewModel: ChatViewModel by viewModels()
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val themeChanged = result.data?.getSerializableExtra(THEME) as Theme?
                if (themeChanged != null) {
                    note.theme = themeChanged
                    return@registerForActivityResult
                }
                // Camera result
                val image = result.data?.extras?.get("data") as Bitmap?
                val selectedTheme = result.data?.getSerializableExtra(THEME) as Theme?
                if (image != null) {
                    handleCameraResult(image)
                } else if (selectedTheme != null) {
                    handleChangeChatTheme(selectedTheme)
                }
            }
        }

    private val messageAdapter by lazy {
        MessageAdapter(this, resources)
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
    private val changeThemeReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val theme = intent?.getSerializableExtra(THEME) as Theme
                handleChangeChatTheme(theme)
            }
        }
    }

    private val onScrollListener by lazy {
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (viewModel.isLoading.value == false) {
                    if (!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                        viewModel.getMessages(note.id, viewModel.page, PAGE_LIMIT)
                    }
                }
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
    }

    override fun onStop() {
        super.onStop()

        unregisterReceiver(messageReceiver)
        unregisterReceiver(changeThemeReceiver)
    }

    override fun onPause() {
        super.onPause()
        messageAdapter.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        messageAdapter.onRelease()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_OK, intent.apply {
            putExtra(THEME, note.theme)
        })
        finish()
    }

    override fun initObservers() {
        super.initObservers()
        observeCurrentUserData()
        observeGetMessagesData()
        observeUploadResultData()
    }

    private fun observeGetMessagesData() {
        viewModel.messagesLiveData.observe(this) { preMessages ->
            messageAdapter.submitList(viewModel.listMessage.map { MessageAdapter.MediaMessage(it) })
            if (preMessages.isEmpty()) {
                binding.messages.removeOnScrollListener(onScrollListener)
            }
        }
    }

    private fun registerReceivers() {
        val messageReceivedIntentFilter = IntentFilter(MESSAGE_RECEIVED)
        registerReceiver(messageReceiver, messageReceivedIntentFilter)
        val changeThemeReceivedIntentFilter = IntentFilter(CHANGE_THEME_RECEIVED)
        registerReceiver(changeThemeReceiver, changeThemeReceivedIntentFilter)
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
            messageAdapter.submitList(viewModel.listMessage.map { MessageAdapter.MediaMessage(it) })
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
                if (requestPermissionToReadGallery(this@ChatActivity)) {
                    BottomSheetGallery(MediaType.IMAGE).apply {
                        show(supportFragmentManager, tag)
                    }
                }
            }
            btnChooseVideo.setOnClickListener {
                if (requestPermissionToReadGallery(this@ChatActivity)) {
                    BottomSheetGallery(MediaType.VIDEO).apply {
                        show(supportFragmentManager, tag)
                    }
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
                val chatInfoIntent = Intent(this@ChatActivity, ChatInfoActivity::class.java).apply {
                    // Put note data (note id, group image url, group name, group theme)
                    putExtra(NOTE, note)
                }
                launcher.launch(chatInfoIntent)
            }
        }
    }

    override fun initViews() {
        super.initViews()
        initMessageRecyclerView()
        handleChangeChatTheme(note.theme)
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
        messageAdapter.submitList(viewModel.listMessage.map { MessageAdapter.MediaMessage(it) })
        binding.messageBox.setText("")
    }

    private fun handleIncomingMessage(message: Message) {
        if (message.room == note.id) {
            viewModel.listMessage.add(message)
            messageAdapter.submitList(viewModel.listMessage.map { MessageAdapter.MediaMessage(it) })
        } else {
            NotificationHelper.pushNotification(
                this@ChatActivity, CHAT_CHANNEL_ID, message
            )
        }
    }

    private fun handleCameraResult(image: Bitmap) {
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

    private fun handleChangeChatTheme(theme: Theme? = null) {
        if (theme != null) {
            // Change background
            Picasso.get().load(theme.bgUrl).into(binding.background)
            // Change primary color in recyclerview chat
            messageAdapter.setPrimaryColor(theme.myMsgBgColor, theme.myMsgTextColor)
            // Change other component in screen
            binding.apply {
                val primaryColor = ColorStateList.valueOf(
                    Color.parseColor(theme.myMsgBgColor)
                )
                btnOpenCamera.imageTintList = primaryColor
                btnChooseImage.imageTintList = primaryColor
                btnChooseVideo.imageTintList = primaryColor
                sendMessageButton.imageTintList = primaryColor
                btnCall.imageTintList = primaryColor
                btnInfo.imageTintList = primaryColor
            }
            // Change status bar color
            window.statusBarColor = Color.parseColor(theme.myMsgBgColor)
            // Change loading progress bar color
            loadingDialog.binding.progressBar.indeterminateTintList = ColorStateList.valueOf(
                Color.parseColor(theme.myMsgBgColor)
            )
        }
    }
}