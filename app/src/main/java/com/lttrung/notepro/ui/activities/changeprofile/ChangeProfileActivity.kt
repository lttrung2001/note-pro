package com.lttrung.notepro.ui.activities.changeprofile

import android.content.Intent
import androidx.activity.viewModels
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityChangeProfileBinding
import com.lttrung.notepro.domain.data.networks.models.UserInfo
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.utils.AppConstant.Companion.USER
import com.lttrung.notepro.utils.ValidationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeProfileActivity : BaseActivity() {
    override val binding by lazy {
        ActivityChangeProfileBinding.inflate(layoutInflater)
    }
    override val viewModel: ChangeProfileViewModel by viewModels()
    private val userInfo by lazy {
        intent.getSerializableExtra(USER) as UserInfo
    }

    private fun validateInputs(
        fullName: String, phoneNumber: String, validationHelper: ValidationHelper
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
        super.initViews()
        binding.tvId.text = userInfo.id
        binding.tvEmail.text = userInfo.email
        binding.tvFullName.setText(userInfo.fullName)
        binding.tvPhoneNumber.setText(userInfo.phoneNumber)
    }

    override fun initListeners() {
        super.initListeners()
        binding.btnChangeProfile.setOnClickListener {
            val fullName = binding.tvFullName.text?.trim().toString()
            val phoneNumber = binding.tvPhoneNumber.text?.trim().toString()
            val helper = validateInputs(fullName, phoneNumber, ValidationHelper())
            if (!helper.hasError) {
                viewModel.changeProfile(fullName, phoneNumber)
            } else {
                // Handle error
            }
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.changeProfileLiveData.observe(this) { userInfo ->
            val resultIntent = Intent()
            resultIntent.putExtra(USER, userInfo)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}