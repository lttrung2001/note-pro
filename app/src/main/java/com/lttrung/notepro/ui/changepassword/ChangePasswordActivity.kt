package com.lttrung.notepro.ui.changepassword

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityChangePasswordBinding
import com.lttrung.notepro.utils.Resource
import com.lttrung.notepro.utils.ValidationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private val changePasswordViewModel: ChangePasswordViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        changePasswordViewModel.changePassword.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnChangePassword.isClickable = false
                    binding.btnChangePassword.showProgress {
                        buttonTextRes = R.string.loading
                        progressColor = Color.WHITE
                    }
                }
                is Resource.Success -> {
                    binding.btnChangePassword.isClickable = true
                    binding.btnChangePassword.hideProgress(R.string.change_password)
                    finish()
                }
                is Resource.Error -> {
                    binding.btnChangePassword.isClickable = true
                    binding.btnChangePassword.hideProgress(R.string.change_password)
                    Log.e("ERROR", resource.message)
                    Snackbar.make(binding.root, resource.message,
                        BaseTransientBottomBar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun initListeners() {
        binding.btnChangePassword.setOnClickListener(changePasswordListener)
        bindProgressButton(binding.btnChangePassword)
        binding.btnChangePassword.attachTextChangeAnimator()
    }

    private fun initViews() {
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
    }

    private val changePasswordListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val oldPassword = binding.currentPassword.text?.trim().toString()
            val newPassword = binding.newPassword.text?.trim().toString()
            val confirmPassword = binding.confirmPassword.text?.trim().toString()
            val helper = ValidationHelper

            if (oldPassword == newPassword) {
                helper.hasError = true
                binding.newPasswordLayout.error = getString(R.string.your_new_password_must_be_different_from_previous_used_passwords)
            } else if (newPassword != confirmPassword) {
                helper.hasError = true
                binding.confirmPasswordLayout.error = getString(R.string.password_not_match)
            } else {
                if (!helper.matchesPasswordLength(oldPassword)) {
                    binding.currentPasswordLayout.error = getString(R.string.password_check)
                }
                if (!helper.matchesPasswordLength(newPassword)) {
                    binding.newPasswordLayout.error = getString(R.string.password_check)
                }
                if (!helper.hasError) {
                    changePasswordViewModel.changePassword(oldPassword, newPassword)
                }
            }
        }
    }
}