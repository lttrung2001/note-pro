package com.lttrung.notepro.ui.forgotpassword

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lttrung.notepro.databinding.ActivityCreateNewPasswordBinding

class CreateNewPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateNewPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCreateNewPasswordBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(binding.root)
    }
}