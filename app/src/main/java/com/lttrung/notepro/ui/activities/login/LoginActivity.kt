package com.lttrung.notepro.ui.activities.login

import android.content.Intent
import android.util.Patterns
import androidx.activity.viewModels
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityLoginBinding
import com.lttrung.notepro.ui.activities.forgotpassword.ForgotPasswordActivity
import com.lttrung.notepro.ui.activities.main.MainActivity
import com.lttrung.notepro.ui.activities.register.RegisterActivity
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.utils.ValidationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    override val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override val viewModel: LoginViewModel by viewModels()

    override fun initObservers() {
        super.initObservers()
        viewModel.refreshTokenLiveData.observe(this) {
            switchToMain()
        }
    }

    override fun initListeners() {
        super.initListeners()
        binding.btnToForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text?.trim().toString()
            val password = binding.edtPassword.text?.trim().toString()
            val helper = ValidationHelper()
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
            if (!helper.hasError) {
                viewModel.login(email, password)
            }
        }

        binding.btnToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun switchToMain() {
        startActivity(Intent(this, MainActivity::class.java).also { intent ->
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}