package com.lttrung.notepro.ui.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityLoginBinding
import com.lttrung.notepro.ui.forgotpassword.ForgotPasswordActivity
import com.lttrung.notepro.ui.main.MainActivity
import com.lttrung.notepro.ui.register.RegisterActivity
import com.lttrung.notepro.utils.Resource
import com.lttrung.notepro.utils.ValidationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    private val btnToForgotPasswordListener: View.OnClickListener by lazy {
        View.OnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private val btnLoginOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val email = binding.edtEmail.text?.trim().toString()
            val password = binding.edtPassword.text?.trim().toString()
            val helper = ValidationHelper
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                helper.hasError = true
                binding.emailLayout.error = getString(R.string.this_text_is_not_email_type)
            }
            if (!helper.matchesPasswordLength(password)) {
                binding.passwordLayout.error = getString(R.string.password_check)
            }
            if (!helper.hasError) {
                viewModel.login(email, password)
            }
        }
    }

    private val btnToRegisterOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        setupListener()
        setupObserver()

        setContentView(binding.root)
    }

    private fun setupObserver() {
        viewModel.login.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnLogin.showProgress {
                        buttonTextRes = R.string.loading
                        progressColor = Color.WHITE
                    }
                    binding.btnLogin.isClickable = false
                }
                is Resource.Success -> {
                    binding.btnLogin.hideProgress(R.string.login)
                    binding.btnLogin.isClickable = true
                    switchToMain()
                }
                is Resource.Error -> {
                    binding.btnLogin.hideProgress(R.string.login)
                    binding.btnLogin.isClickable = true
                    binding.passwordLayout.error = resource.message
                }
            }
        }
    }

    private fun setupListener() {
        binding.btnToForgotPassword.setOnClickListener(btnToForgotPasswordListener)
        binding.btnLogin.setOnClickListener(btnLoginOnClickListener)
        binding.btnToRegister.setOnClickListener(btnToRegisterOnClickListener)

        bindProgressButton(binding.btnLogin)
        binding.btnLogin.attachTextChangeAnimator()
    }

    private fun switchToMain() {
        startActivity(Intent(this, MainActivity::class.java).also { intent ->
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}