package com.lttrung.notepro.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.lttrung.notepro.databinding.ActivitySettingBinding
import com.lttrung.notepro.ui.login.LoginActivity

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    private val logoutOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener { view ->
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnLogout.setOnClickListener(logoutOnClickListener)

        setContentView(binding.root)
    }
}