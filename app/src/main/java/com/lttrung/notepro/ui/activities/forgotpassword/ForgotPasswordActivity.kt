package com.lttrung.notepro.ui.activities.forgotpassword

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityForgotPasswordBinding
import com.lttrung.notepro.ui.activities.resetpassword.ResetPasswordActivity
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordActivity : BaseActivity() {
    override val binding by lazy {
        ActivityForgotPasswordBinding.inflate(layoutInflater)
    }
    override val viewModel: ForgotPasswordViewModel by viewModels()

    override fun initObservers() {
        super.initObservers()
        viewModel.forgotPasswordLiveData.observe(this) {
            val resetPasswordIntent =
                Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
            resetPasswordIntent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(resetPasswordIntent)
        }
    }

    override fun initListeners() {
        super.initListeners()
        binding.btnSendInstructions.setOnClickListener {
            val email = binding.edtEmail.text?.trim().toString()
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailLayout.error = getString(R.string.this_text_is_not_email_type)
            } else {
                viewModel.forgotPassword(email)
            }
        }
    }
}