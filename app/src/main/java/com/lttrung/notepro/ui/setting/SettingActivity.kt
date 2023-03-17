package com.lttrung.notepro.ui.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.lttrung.notepro.database.data.locals.UserLocals
import com.lttrung.notepro.databinding.ActivitySettingBinding
import com.lttrung.notepro.ui.changepassword.ChangePasswordActivity
import com.lttrung.notepro.ui.login.LoginActivity
import com.lttrung.notepro.ui.viewprofile.ViewProfileActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private val settingViewModel: SettingViewModel by viewModels()

    private val viewProfileListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val viewProfileIntent = Intent(this, ViewProfileActivity::class.java)
            startActivity(viewProfileIntent)
        }
    }

    private val changePasswordListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val changePasswordIntent = Intent(this, ChangePasswordActivity::class.java)
            startActivity(changePasswordIntent)
        }
    }

    private val logoutOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            settingViewModel.logout()
            val logoutIntent = Intent(this, LoginActivity::class.java)
            logoutIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(logoutIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initListeners()
        initObservers()
    }

    override fun onResume() {
        super.onResume()
        settingViewModel.getCurrentUserInfo()
    }

    private fun initObservers() {
        settingViewModel.userLiveData.observe(this) { user ->
            binding.tvName.text = user.fullName
        }
    }

    private fun initListeners() {
        binding.btnViewProfile.setOnClickListener(viewProfileListener)
        binding.btnChangePassword.setOnClickListener(changePasswordListener)
        binding.btnLogout.setOnClickListener(logoutOnClickListener)
    }

    private fun initViews() {
        binding = ActivitySettingBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return true
    }
}