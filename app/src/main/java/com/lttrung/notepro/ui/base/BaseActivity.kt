package com.lttrung.notepro.ui.base

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.lttrung.notepro.ui.dialogs.LoadingDialog

abstract class BaseActivity : AppCompatActivity() {
    abstract val binding: ViewBinding
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
                if (!loadingDialog.isShowing)
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