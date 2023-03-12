package com.lttrung.notepro.ui.resetpassword

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityResetPasswordBinding
import com.lttrung.notepro.ui.login.LoginActivity
import com.lttrung.notepro.utils.Resource
import com.lttrung.notepro.utils.ValidationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResetPasswordBinding
    private val viewModel: ResetPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        viewModel.resetPassword.observe(this) { resource ->
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
                }
            }
        }
    }

    private fun initListeners() {
        binding.btnResetPassword.setOnClickListener(resetPasswordListener)
    }

    private fun initViews() {
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private val resetPasswordListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val code = binding.edtCode.text?.trim().toString()
            val password = binding.edtPassword.text?.trim().toString()
            val helper = ValidationHelper
            if (code.isBlank()) {
                helper.hasError = true
                binding.codeLayout.error = getString(R.string.code_check)
            }
            if (!helper.matchesPasswordLength(password)) {
                binding.passwordLayout.error = getString(R.string.password_check)
            }
            if (!helper.hasError) {
                viewModel.resetPassword(code, password)
            }
        }
    }
}