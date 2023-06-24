package com.lttrung.notepro.ui.activities.changepassword

import androidx.activity.viewModels
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityChangePasswordBinding
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.utils.ValidationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangePasswordActivity : BaseActivity() {
    override val binding by lazy {
        ActivityChangePasswordBinding.inflate(layoutInflater)
    }
    override val viewModel: ChangePasswordViewModel by viewModels()

    override fun initObservers() {
        super.initObservers()
        viewModel.changePasswordLiveData.observe(this) { resource ->
            finish()
        }
    }

    override fun initListeners() {
        super.initListeners()
        binding.btnChangePassword.setOnClickListener {
            val oldPassword = binding.currentPassword.text?.trim().toString()
            val newPassword = binding.newPassword.text?.trim().toString()
            val confirmPassword = binding.confirmPassword.text?.trim().toString()
            val helper =
                validateInputs(oldPassword, newPassword, confirmPassword, ValidationHelper())
            if (!helper.hasError) {
                viewModel.changePassword(oldPassword, newPassword)
            }
        }
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