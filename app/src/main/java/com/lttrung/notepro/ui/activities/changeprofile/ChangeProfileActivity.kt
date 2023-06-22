package com.lttrung.notepro.ui.activities.changeprofile

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityChangeProfileBinding
import com.lttrung.notepro.domain.data.networks.models.UserInfo
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.utils.AppConstant.Companion.USER
import com.lttrung.notepro.utils.Resource
import com.lttrung.notepro.utils.ValidationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeProfileActivity : BaseActivity() {
    override val binding by lazy {
        ActivityChangeProfileBinding.inflate(layoutInflater)
    }
    private val changeProfileViewModel: ChangeProfileViewModel by viewModels()
    private val userInfo by lazy {
        intent.getSerializableExtra(USER) as UserInfo
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun initViews() {
        binding.tvId.text = userInfo.id
        binding.tvEmail.text = userInfo.email
        binding.tvFullName.setText(userInfo.fullName)
        binding.tvPhoneNumber.setText(userInfo.phoneNumber)
    }

    override fun initListeners() {
        binding.btnChangeProfile.setOnClickListener {
            val fullName = binding.tvFullName.text?.trim().toString()
            val phoneNumber = binding.tvPhoneNumber.text?.trim().toString()
            val helper = validateInputs(fullName, phoneNumber, ValidationHelper())
            if (!helper.hasError) {
                changeProfileViewModel.changeProfile(fullName, phoneNumber)
            } else {
                // Handle error
            }
        }
    }

    override fun initObservers() {
        changeProfileViewModel.changeProfileLiveData.observe(this) { resource ->
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
}