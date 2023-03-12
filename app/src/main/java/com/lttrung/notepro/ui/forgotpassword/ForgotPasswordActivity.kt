package com.lttrung.notepro.ui.forgotpassword

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
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
    private lateinit var binding: ActivityForgotPasswordBinding
    private val viewModel: ForgotPasswordViewModel by viewModels()

    private val sendInstructionsListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val email = binding.edtEmail.text?.trim().toString()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailLayout.error = getString(R.string.this_text_is_not_email_type)
            } else {
                viewModel.forgotPassword(email)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        viewModel.forgotPassword.observe(this) { resource ->
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
                    binding.emailLayout.error = resource.message
                }
            }
        }
    }

    private fun initListeners() {
        binding.btnSendInstructions.setOnClickListener(sendInstructionsListener)
    }

    private fun initViews() {
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}