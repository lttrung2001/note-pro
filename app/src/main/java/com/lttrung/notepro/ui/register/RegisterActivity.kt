package com.lttrung.notepro.ui.register

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
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityRegisterBinding
import com.lttrung.notepro.utils.Resource
import com.lttrung.notepro.utils.ValidationHelper
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
            val helper = ValidationHelper()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                helper.hasError = true
                binding.emailLayout.error = getString(R.string.this_text_is_not_email_type)
            }
            if (!helper.matchesPasswordLength(password)) {
                binding.passwordLayout.error = getString(R.string.password_check)
            }
            if (!helper.matchesPasswordLength(confirmPassword)) {
                binding.confirmPasswordLayout.error = getString(R.string.password_check)
            }
            if (!helper.matchesFullName(fullName)) {
                binding.fullNameLayout.error = getString(R.string.invalid_full_name)
            }
            if (!helper.matchesPhoneNumber(phoneNumber)) {
                binding.phoneNumberLayout.error = getString(R.string.phone_number_check)
            }
            if (!helper.hasError) {
                if (password == confirmPassword) {
                    viewModel.register(email, password, fullName, phoneNumber)
                } else {
                    binding.confirmPasswordLayout.error = getString(R.string.password_not_match)
                }
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
                    Snackbar.make(binding.root, resource.message,
                        BaseTransientBottomBar.LENGTH_LONG
                    ).show()
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