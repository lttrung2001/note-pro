package com.lttrung.notepro.ui.changepassword

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lttrung.notepro.databinding.ActivityChangePasswordBinding
import com.lttrung.notepro.utils.Resource
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

                }
                is Resource.Success -> {
                    finish()
                }
                is Resource.Error -> {
                    Log.e("ERROR", resource.message)
                }
            }
        }
    }

    private fun initListeners() {
        binding.btnChangePassword.setOnClickListener(changePasswordListener)
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
            if (oldPassword == newPassword) {
                changePasswordViewModel.changePassword.postValue(Resource.Error("Password must be different!"))
            } else if (newPassword != confirmPassword) {
                changePasswordViewModel.changePassword.postValue(Resource.Error("New password not match!"))
            } else {
                changePasswordViewModel.changePassword(
                    oldPassword, newPassword
                )
            }
        }
    }
}