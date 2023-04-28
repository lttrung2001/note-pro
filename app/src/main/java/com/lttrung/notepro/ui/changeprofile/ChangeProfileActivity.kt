package com.lttrung.notepro.ui.changeprofile

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityChangeProfileBinding
import com.lttrung.notepro.domain.data.networks.models.UserInfo
import com.lttrung.notepro.utils.AppConstant.Companion.USER
import com.lttrung.notepro.utils.Resource
import com.lttrung.notepro.utils.ValidationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeProfileActivity : AppCompatActivity() {
    private val binding: ActivityChangeProfileBinding by lazy {
        ActivityChangeProfileBinding.inflate(layoutInflater)
    }
    private val changeProfileViewModel: ChangeProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initData()
        initObserver()
    }

    private fun initObserver() {
        changeProfileViewModel.changeProfile.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    val resultIntent = Intent()
                    val user = resource.data
                    resultIntent.putExtra(USER, user)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
                is Resource.Error -> {
                    Snackbar.make(
                        binding.root, resource.t.message.toString(),
                        BaseTransientBottomBar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun initData() {
        val user = intent.getSerializableExtra(USER) as UserInfo
        binding.tvId.text = user.id
        binding.tvEmail.text = user.email
        binding.tvFullName.setText(user.fullName)
        binding.tvPhoneNumber.setText(user.phoneNumber)
    }

    private fun initViews() {
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_change_profile, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save_info -> {
                val fullName = binding.tvFullName.text?.trim().toString()
                val phoneNumber = binding.tvPhoneNumber.text?.trim().toString()
                val helper = ValidationHelper()
                if (!helper.matchesFullName(fullName)) {
                    binding.tvFullName.error = getString(R.string.invalid_full_name)
                }
                if (!helper.matchesPhoneNumber(phoneNumber)) {
                    binding.tvPhoneNumber.error = getString(R.string.phone_number_check)
                }
                if (!helper.hasError) {
                    val user = intent.getSerializableExtra(USER) as UserInfo
                    intent.putExtra(USER, UserInfo(user.id, user.email, fullName, phoneNumber))
                    changeProfileViewModel.changeProfile(fullName, phoneNumber)
                }
            }
            else -> {
                finish()
            }
        }
        return true
    }
}