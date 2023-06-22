package com.lttrung.notepro.ui.activities.register

import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityRegisterBinding
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.utils.Resource
import com.lttrung.notepro.utils.ValidationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterActivity : BaseActivity() {
    override val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initViews() {
        bindProgressButton(binding.btnRegister)
        binding.btnRegister.attachTextChangeAnimator()
    }

    override fun initObservers() {
        viewModel.registerLiveData.observe(this) { resource ->
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
                    Toast.makeText(this, resource.t.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun initListeners() {
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
        }
        if (!helper.matchesPasswordLength(password)) {
            binding.passwordLayout.error = getString(R.string.password_check)
        }
        if (!helper.matchesPasswordLength(confirmPassword)) {
            binding.confirmPasswordLayout.error = getString(R.string.password_check)
        }
        if (!helper.matchesConfirmPassword(password, confirmPassword)) {
            binding.confirmPasswordLayout.error = getString(R.string.password_not_match)
        }
        if (!helper.matchesFullName(fullName)) {
            binding.fullNameLayout.error = getString(R.string.invalid_full_name)
        }
        if (!helper.matchesPhoneNumber(phoneNumber)) {
            binding.phoneNumberLayout.error = getString(R.string.phone_number_check)
        }
        return helper
    }
}