package com.lttrung.notepro.ui.activities.register

import android.util.Patterns
import androidx.activity.viewModels
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityRegisterBinding
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.utils.ValidationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : BaseActivity() {
    override val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    override val viewModel: RegisterViewModel by viewModels()

    override fun initObservers() {
        super.initObservers()
        viewModel.registerLiveData.observe(this) { resource ->
            finish()
        }
    }

    override fun initListeners() {
        super.initListeners()
        binding.btnToLogin.setOnClickListener {
            finish()
        }
        binding.btnRegister.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            val confirmPassword = binding.edtConfirmPassword.text.toString()
            val fullName = binding.edtFullName.text.toString()
            val phoneNumber = binding.edtPhoneNumber.text.toString()
            val helper = invalidateInputs(
                email,
                password,
                confirmPassword,
                fullName,
                phoneNumber,
                ValidationHelper()
            )

            if (!helper.hasError) {
                viewModel.register(email, password, fullName, phoneNumber)
            }
        }
    }

    private fun invalidateInputs(
        email: String,
        password: String,
        confirmPassword: String,
        fullName: String,
        phoneNumber: String,
        helper: ValidationHelper
    ): ValidationHelper {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            helper.hasError = true
            binding.emailLayout.error = getString(R.string.this_text_is_not_email_type)
        } else {
            binding.emailLayout.error = ""
        }
        if (!helper.matchesPasswordLength(password)) {
            binding.passwordLayout.error = getString(R.string.password_check)
        } else {
            binding.passwordLayout.error = ""
        }
        if (!helper.matchesPasswordLength(confirmPassword)) {
            binding.confirmPasswordLayout.error = getString(R.string.password_check)
        } else {
            binding.confirmPasswordLayout.error = ""
        }
        if (!helper.matchesConfirmPassword(password, confirmPassword)) {
            binding.confirmPasswordLayout.error = getString(R.string.password_not_match)
        } else {
            binding.confirmPasswordLayout.error = ""
        }
        if (!helper.matchesFullName(fullName)) {
            binding.fullNameLayout.error = getString(R.string.invalid_full_name)
        } else {
            binding.fullNameLayout.error = ""
        }
        if (phoneNumber.isEmpty()) {
            helper.hasError = true
            binding.phoneNumberLayout.error = getString(R.string.phone_number_can_not_be_empty)
        } else if (!helper.matchesPhoneNumber(phoneNumber)) {
            binding.phoneNumberLayout.error = getString(R.string.phone_number_check)
        } else {
            binding.phoneNumberLayout.error = ""
        }
        return helper
    }
}