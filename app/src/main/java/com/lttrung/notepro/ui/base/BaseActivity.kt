package com.lttrung.notepro.ui.base

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.lttrung.notepro.ui.activities.chat.ChatSocketService
import com.lttrung.notepro.ui.dialogs.LoadingDialog

abstract class BaseActivity : AppCompatActivity() {
    abstract val binding: ViewBinding

    lateinit var socketService: ChatSocketService
    val connection = object : ServiceConnection {
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
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
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
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        initObservers()
        initListeners()
    }
}