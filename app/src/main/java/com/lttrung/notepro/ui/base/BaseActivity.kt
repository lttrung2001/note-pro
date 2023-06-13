package com.lttrung.notepro.ui.base

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity : AppCompatActivity() {
    abstract val binding: ViewBinding
    abstract fun initViews()
    abstract fun initListeners()
    abstract fun initObservers()
}