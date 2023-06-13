package com.lttrung.notepro.ui.activities.changepassword

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityChangePasswordBinding
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.utils.Resource
import com.lttrung.notepro.utils.ValidationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordActivity : BaseActivity() {
    override val binding by lazy {
        ActivityChangePasswordBinding.inflate(layoutInflater)
    }
    private val changePasswordViewModel: ChangePasswordViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initListeners()
        initObservers()
    }

    override fun initObservers() {
        changePasswordViewModel.changePasswordLiveData.observe(this) { resource ->
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
                    Snackbar.make(
                        binding.root, resource.t.message.toString(), Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun initListeners() {
        bindProgressButton(binding.btnChangePassword)
        binding.btnChangePassword.attachTextChangeAnimator()
        binding.btnChangePassword.setOnClickListener {
            val oldPassword = binding.currentPassword.text?.trim().toString()
            val newPassword = binding.newPassword.text?.trim().toString()
            val confirmPassword = binding.confirmPassword.text?.trim().toString()
            val helper =
                validateInputs(oldPassword, newPassword, confirmPassword, ValidationHelper())
            if (!helper.hasError) {
                changePasswordViewModel.changePassword(oldPassword, newPassword)
            }
        }
    }

    override fun initViews() {
        setContentView(binding.root)
    }

    private fun validateInputs(
        oldPassword: String, newPassword: String, confirmPassword: String, helper: ValidationHelper
    ): ValidationHelper {
        if (oldPassword == newPassword) {
            helper.hasError = true
            binding.newPasswordLayout.error =
                getString(R.string.your_new_password_must_be_different_from_previous_used_passwords)
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
        }
        return helper
    }
}