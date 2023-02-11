package com.lttrung.notepro.ui.forgotpassword

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lttrung.notepro.databinding.ActivitySendInstructionsResultBinding
import com.lttrung.notepro.ui.login.LoginActivity


class SendInstructionsResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySendInstructionsResultBinding

    private val openEmailAppListener: View.OnClickListener by lazy {
        View.OnClickListener { view ->

        }
    }

    private val skipListener: View.OnClickListener by lazy {
        View.OnClickListener { view ->
            startActivity(Intent(this, LoginActivity::class.java).also { intent ->
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySendInstructionsResultBinding.inflate(layoutInflater)

        binding.btnOpenEmailApp.setOnClickListener(openEmailAppListener)
        binding.btnSkip.setOnClickListener(skipListener)
        binding.btnTryAnotherEmail.setOnClickListener {
            onBackPressed()
        }

        setContentView(binding.root)
    }
}