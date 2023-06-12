package com.lttrung.notepro.ui.activities.changeprofile

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
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
    private val binding by lazy {
        ActivityChangeProfileBinding.inflate(layoutInflater)
    }
    private val changeProfileViewModel: ChangeProfileViewModel by viewModels()
    private val userInfo by lazy {
        intent.getSerializableExtra(USER) as UserInfo
    }
    private val alertDialog by lazy {
        val builder = AlertDialog.Builder(this)
        builder.setView(layoutInflater.inflate(R.layout.dialog_loading, null))
        builder.setCancelable(false)
        builder.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initData()
        initObserver()
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
                val helper = validateInputs(fullName, phoneNumber, ValidationHelper())
                if (!helper.hasError) {
                    intent.putExtra(
                        USER,
                        UserInfo(userInfo.id, userInfo.email, fullName, phoneNumber)
                    )
                    changeProfileViewModel.changeProfile(fullName, phoneNumber)
                }
            }

            else -> {
                finish()
            }
        }
        return true
    }

    private fun validateInputs(
        fullName: String,
        phoneNumber: String,
        validationHelper: ValidationHelper
    ): ValidationHelper {
        if (!validationHelper.matchesFullName(fullName)) {
            binding.tvFullName.error = getString(R.string.invalid_full_name)
        }
        if (!validationHelper.matchesPhoneNumber(phoneNumber)) {
            binding.tvPhoneNumber.error = getString(R.string.phone_number_check)
        }
        return validationHelper
    }

    private fun initObserver() {
        changeProfileViewModel.changeProfileLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    alertDialog.show()
                }

                is Resource.Success -> {
                    alertDialog.dismiss()
                    val resultIntent = Intent()
                    val user = resource.data
                    resultIntent.putExtra(USER, user)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }

                is Resource.Error -> {
                    alertDialog.dismiss()
                    Snackbar.make(
                        binding.root, resource.t.message.toString(),
                        BaseTransientBottomBar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun initData() {
        binding.tvId.text = userInfo.id
        binding.tvEmail.text = userInfo.email
        binding.tvFullName.setText(userInfo.fullName)
        binding.tvPhoneNumber.setText(userInfo.phoneNumber)
    }

    private fun initViews() {
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}