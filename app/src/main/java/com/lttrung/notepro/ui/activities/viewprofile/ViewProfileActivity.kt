package com.lttrung.notepro.ui.activities.viewprofile

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.databinding.ActivityViewProfileBinding
import com.lttrung.notepro.domain.data.networks.models.UserInfo
import com.lttrung.notepro.ui.activities.changeprofile.ChangeProfileActivity
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.utils.AppConstant.Companion.USER
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewProfileActivity : BaseActivity() {
    override val binding by lazy {
        ActivityViewProfileBinding.inflate(layoutInflater)
    }
    private val viewProfileViewModel: ViewProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initObservers()
        initListeners()
        viewProfileViewModel.getProfile()
    }

    override fun initObservers() {
        viewProfileViewModel.profileLiveData.observe(this) { resource ->
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
                    Snackbar.make(
                        binding.root, resource.t.message.toString(), Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun initViews() {
        setContentView(binding.root)
    }

    override fun initListeners() {
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