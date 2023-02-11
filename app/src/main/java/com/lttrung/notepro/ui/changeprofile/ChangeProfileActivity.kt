package com.lttrung.notepro.ui.changeprofile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityChangeProfileBinding

class ChangeProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChangeProfileBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_change_profile, menu)
        return true
    }
}