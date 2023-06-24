package com.lttrung.notepro.ui.activities.viewprofile

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.lttrung.notepro.databinding.ActivityViewProfileBinding
import com.lttrung.notepro.domain.data.networks.models.UserInfo
import com.lttrung.notepro.ui.activities.changeprofile.ChangeProfileActivity
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.utils.AppConstant.Companion.USER
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewProfileActivity : BaseActivity() {
    override val binding by lazy {
        ActivityViewProfileBinding.inflate(layoutInflater)
    }
    override val viewModel: ViewProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getProfile()
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.profileLiveData.observe(this) { user ->
            binding.tvId.text = user.id
            binding.tvFullName.text = user.fullName
            binding.tvEmail.text = user.email
            binding.tvPhoneNumber.text = user.phoneNumber
        }
    }

    override fun initListeners() {
        super.initListeners()
        binding.btnEdit.setOnClickListener {
            val user = UserInfo(
                binding.tvId.text.toString(),
                binding.tvEmail.text.toString(),
                binding.tvFullName.text.toString(),
                binding.tvPhoneNumber.text.toString()
            )
            val editProfileIntent = Intent(this, ChangeProfileActivity::class.java).apply {
                putExtra(USER, user)
            }
            launcher.launch(editProfileIntent)
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultIntent = result.data
                resultIntent?.let {
                    val user = resultIntent.getSerializableExtra(USER) as UserInfo
                    binding.tvFullName.text = user.fullName
                    binding.tvPhoneNumber.text = user.phoneNumber
                }
            }
        }
}