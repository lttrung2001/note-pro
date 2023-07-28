package com.lttrung.notepro.ui.base

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.lttrung.notepro.R
import com.lttrung.notepro.ui.activities.chat.ChatSocketService
import com.lttrung.notepro.ui.activities.jitsi.JitsiActivity
import com.lttrung.notepro.ui.dialogs.LoadingDialog
import com.lttrung.notepro.ui.dialogs.builders.DialogBuilder
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions

abstract class BaseActivity : AppCompatActivity() {
    abstract val binding: ViewBinding

    lateinit var socketService: ChatSocketService
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ChatSocketService.LocalBinder
            socketService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, ChatSocketService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_IMPORTANT)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
    }

    open fun initViews() {

    }

    open fun initListeners() {

    }

    open val viewModel: BaseViewModel by viewModels()
    val loadingDialog by lazy {
        LoadingDialog(this@BaseActivity)
    }
    private val errorDialog by lazy {
        DialogBuilder(this)
            .setContent("Đã có lỗi xảy ra. Vui lòng thử lại sau.")
            .build()
    }

    open fun initObservers() {
        viewModel.isLoading.observe(this@BaseActivity) { isLoading ->
            if (isLoading) {
                loadingDialog.show()
            } else {
                loadingDialog.dismiss()
            }
        }
        viewModel.throwableLiveData.observe(this@BaseActivity) {
            // Show dialog
            if (!errorDialog.isShowing) {
                errorDialog.show()
            }
        }
    }

    fun startJitsi(options: JitsiMeetConferenceOptions) {
        val jitsiIntent = Intent(this@BaseActivity, JitsiActivity::class.java)
        jitsiIntent.action = "org.jitsi.meet.CONFERENCE"
        jitsiIntent.putExtra("JitsiMeetConferenceOptions", options)
        jitsiIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(jitsiIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initObservers()
        initListeners()
    }
}