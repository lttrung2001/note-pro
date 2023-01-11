package com.lttrung.notepro.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lttrung.notepro.databinding.ActivityLoginBinding
import com.lttrung.notepro.ui.main.MainActivity
import com.lttrung.notepro.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val btnLoginOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener {
            startActivity(Intent(this, MainActivity::class.java).also { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
    }

    private val btnToRegisterOnClickListener: View.OnClickListener by lazy {
        View.OnClickListener { view ->
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)

        binding.btnLogin.setOnClickListener(btnLoginOnClickListener)
        binding.btnToRegister.setOnClickListener(btnToRegisterOnClickListener)

        setContentView(binding.root)
    }
}