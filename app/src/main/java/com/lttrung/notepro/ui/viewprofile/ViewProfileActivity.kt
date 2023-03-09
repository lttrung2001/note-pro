package com.lttrung.notepro.ui.viewprofile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.User
import com.lttrung.notepro.databinding.ActivityViewProfileBinding
import com.lttrung.notepro.ui.changeprofile.ChangeProfileActivity
import com.lttrung.notepro.utils.AppConstant.Companion.USER
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewProfileBinding
    private val viewProfileViewModel: ViewProfileViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initObserver()
        viewProfileViewModel.getProfile()
    }

    private fun initObserver() {
        viewProfileViewModel.profile.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    val user = resource.data
                    binding.tvId.text = user.id
                    binding.tvFullName.text = user.fullName
                    binding.tvEmail.text = user.email
                    binding.tvPhoneNumber.text = user.phoneNumber
                }
                is Resource.Error -> {
                    Log.e("ERROR", resource.message)
                }
            }
        }
    }

    private fun initViews() {
        binding = ActivityViewProfileBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_view_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit_info -> {
                val editProfileIntent = Intent(this, ChangeProfileActivity::class.java)
                val user = User(
                    binding.tvId.text.toString(),
                    binding.tvEmail.text.toString(),
                    binding.tvFullName.text.toString(),
                    binding.tvPhoneNumber.text.toString()
                )
                editProfileIntent.putExtra(USER, user)
                launcher.launch(editProfileIntent)
            }
            else -> {
                onBackPressed()
            }
        }
        return true
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultIntent = result.data
                resultIntent?.let {
                    val user = resultIntent.getSerializableExtra(USER) as User
                    binding.tvFullName.text = user.fullName
                    binding.tvPhoneNumber.text = user.phoneNumber
                }
            }
        }
}