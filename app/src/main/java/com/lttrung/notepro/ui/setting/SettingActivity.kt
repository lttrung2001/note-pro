package com.lttrung.notepro.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lttrung.notepro.databinding.ActivitySettingBinding
import com.lttrung.notepro.ui.changepassword.ChangePasswordActivity
import com.lttrung.notepro.ui.login.LoginActivity
import com.lttrung.notepro.ui.viewprofile.ViewProfileActivity

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding

    private val viewProfileListener: View.OnClickListener by lazy {
        View.OnClickListener { view ->
            val intent = Intent(this, ViewProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private val changePasswordListener: View.OnClickListener by lazy {
        View.OnClickListener { view ->
            val intent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private val logoutOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener { view ->
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnViewProfile.setOnClickListener(viewProfileListener)
        binding.btnChangePassword.setOnClickListener(changePasswordListener)
        binding.btnLogout.setOnClickListener(logoutOnClickListener)

        setContentView(binding.root)
    }
}