package com.lttrung.notepro.ui.resetpassword

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
import com.lttrung.notepro.ui.login.LoginActivity
import com.lttrung.notepro.utils.Resource
import com.lttrung.notepro.utils.ValidationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityResetPasswordBinding.inflate(layoutInflater)
    }
    private val viewModel: ResetPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initListeners()
        initObservers()
    }

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

    private fun initObservers() {
        viewModel.resetPasswordLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnResetPassword.isClickable = false
                    binding.btnResetPassword.showProgress {
                        buttonTextRes = R.string.loading
                        progressColor = Color.WHITE
                    }
                }

                is Resource.Success -> {
                    binding.btnResetPassword.hideProgress(R.string.reset_password)
                    binding.btnResetPassword.isClickable = true
                    val loginIntent =
                        Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                    loginIntent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(loginIntent)
                }

                is Resource.Error -> {
                    binding.btnResetPassword.hideProgress(R.string.reset_password)
                    binding.btnResetPassword.isClickable = true
                    Snackbar.make(
                        binding.root, resource.t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun initListeners() {
        binding.btnResetPassword.setOnClickListener {
            val code = binding.edtCode.text?.trim().toString()
            val password = binding.edtPassword.text?.trim().toString()
            val helper = validateInputs(code, password, ValidationHelper())
            if (!helper.hasError) {
                viewModel.resetPassword(code, password)
            }
        }
    }

    private fun initViews() {
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}