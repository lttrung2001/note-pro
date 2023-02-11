package com.lttrung.notepro.ui.forgotpassword

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lttrung.notepro.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    private val sendInstructionsListener: View.OnClickListener by lazy {
        View.OnClickListener { view ->
//            startActivity(Intent(this, SendInstructionsResultActivity::class.java))
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnSendInstructions.setOnClickListener(sendInstructionsListener)

        setContentView(binding.root)
    }
}