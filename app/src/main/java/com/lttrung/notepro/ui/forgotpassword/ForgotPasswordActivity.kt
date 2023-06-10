package com.lttrung.notepro.ui.forgotpassword

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityForgotPasswordBinding
import com.lttrung.notepro.ui.resetpassword.ResetPasswordActivity
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityForgotPasswordBinding.inflate(layoutInflater)
    }
    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        viewModel.forgotPasswordLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnSendInstructions.showProgress {
                        buttonTextRes = R.string.loading
                        progressColor = Color.WHITE
                    }
                    binding.btnSendInstructions.isClickable = false
                }
                is Resource.Success -> {
                    binding.btnSendInstructions.hideProgress(R.string.send_instructions)
                    binding.btnSendInstructions.isClickable = true
                    val resetPasswordIntent =
                        Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                    resetPasswordIntent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(resetPasswordIntent)
                }
                is Resource.Error -> {
                    binding.btnSendInstructions.hideProgress(R.string.send_instructions)
                    binding.btnSendInstructions.isClickable = true
                    binding.emailLayout.error = resource.t.message.toString()
                }
            }
        }
    }

    private fun initListeners() {
        binding.btnSendInstructions.setOnClickListener {
            val email = binding.edtEmail.text?.trim().toString()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailLayout.error = getString(R.string.this_text_is_not_email_type)
            } else {
                viewModel.forgotPassword(email)
            }
        }
    }

    private fun initViews() {
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}