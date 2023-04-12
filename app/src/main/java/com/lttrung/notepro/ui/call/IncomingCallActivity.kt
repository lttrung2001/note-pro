package com.lttrung.notepro.ui.call

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lttrung.notepro.databinding.ActivityIncomingCallBinding

class IncomingCallActivity : AppCompatActivity() {
    private val binding: ActivityIncomingCallBinding by lazy {
        ActivityIncomingCallBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setListener()
    }

    private fun setListener() {
    }
}