package com.lttrung.notepro.ui.activities.resetpassword

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityResetPasswordBinding
import com.lttrung.notepro.ui.activities.login.LoginActivity
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.utils.Resource
import com.lttrung.notepro.utils.ValidationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordActivity : BaseActivity() {
    override val binding by lazy {
        ActivityResetPasswordBinding.inflate(layoutInflater)
    }
    override val viewModel: ResetPasswordViewModel by viewModels()

    private fun validateInputs(
        code: String,
        password: String,
        validationHelper: ValidationHelper
    ): ValidationHelper {
        if (code.isBlank()) {
            validationHelper.hasError = true
            binding.codeLayout.error = getString(R.string.code_check)
        }
        if (!validationHelper.matchesPasswordLength(password)) {
            binding.passwordLayout.error = getString(R.string.password_check)
        }
        return validationHelper
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.resetPasswordLiveData.observe(this) { resource ->
            val loginIntent =
                Intent(this@ResetPasswordActivity, LoginActivity::class.java)
            loginIntent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(loginIntent)
        }
    }

    override fun initListeners() {
        super.initListeners()
        binding.btnResetPassword.setOnClickListener {
            val code = binding.edtCode.text?.trim().toString()
            val password = binding.edtPassword.text?.trim().toString()
            val helper = validateInputs(code, password, ValidationHelper())
            if (!helper.hasError) {
                viewModel.resetPassword(code, password)
            }
        }
    }
}