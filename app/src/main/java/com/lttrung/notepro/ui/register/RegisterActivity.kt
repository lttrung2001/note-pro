package com.lttrung.notepro.ui.register

import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityRegisterBinding
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    private val btnToLoginOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            finish()
        }
    }

    private val btnRegisterOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            val confirmPassword = binding.edtConfirmPassword.text.toString()
            val fullName = binding.edtFullName.text.toString()
            val phoneNumber = binding.edtPhoneNumber.text.toString()
            if (email.isBlank() || password.isBlank() || confirmPassword.isBlank() || fullName.isBlank() || phoneNumber.isBlank()) {
                binding.edtPassword.error = getString(R.string.all_input_required)
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtPassword.error = getString(R.string.this_text_is_not_email_type)
            } else if (!password.equals(confirmPassword)) {
                binding.edtPassword.error = getString(R.string.password_not_match)
            } else {
                viewModel.register(email, password, fullName, phoneNumber)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        setupListener()
        setupObserver()
    }

    private fun initViews() {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setupObserver() {
        viewModel.register.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnRegister.isClickable = false
                    binding.btnRegister.showProgress {
                        buttonTextRes = R.string.loading
                        progressColor = Color.WHITE
                    }
                }
                is Resource.Success -> {
                    binding.btnRegister.isClickable = true
                    binding.btnRegister.hideProgress(R.string.register)
                    finish()
                }
                is Resource.Error -> {
                    binding.btnRegister.isClickable = true
                    binding.btnRegister.hideProgress(R.string.register)
                }
            }
        }
    }

    private fun setupListener() {
        binding.btnToLogin.setOnClickListener(btnToLoginOnClickListener)
        binding.btnRegister.setOnClickListener(btnRegisterOnClickListener)

        bindProgressButton(binding.btnRegister)
        binding.btnRegister.attachTextChangeAnimator()
    }
}