package com.lttrung.notepro.ui.register

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lttrung.notepro.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val btnToLoginOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener { view ->
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)

        binding.btnToLogin.setOnClickListener(btnToLoginOnClickListener)

        setContentView(binding.root)
    }
}