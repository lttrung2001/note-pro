package com.lttrung.notepro.ui.activities.viewprofile

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityViewProfileBinding
import com.lttrung.notepro.domain.data.networks.models.UserInfo
import com.lttrung.notepro.ui.activities.changeprofile.ChangeProfileActivity
import com.lttrung.notepro.utils.AppConstant.Companion.USER
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewProfileActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityViewProfileBinding.inflate(layoutInflater)
    }
    private val viewProfileViewModel: ViewProfileViewModel by viewModels()
    private val alertDialog by lazy {
        val builder = AlertDialog.Builder(this)
        builder.setView(layoutInflater.inflate(R.layout.dialog_loading, null))
        builder.setCancelable(false)
        builder.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initObserver()
        viewProfileViewModel.getProfile()
    }

    private fun initObserver() {
        viewProfileViewModel.profileLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    alertDialog.show()
                }

                is Resource.Success -> {
                    alertDialog.dismiss()
                    val user = resource.data
                    binding.tvId.text = user.id
                    binding.tvFullName.text = user.fullName
                    binding.tvEmail.text = user.email
                    binding.tvPhoneNumber.text = user.phoneNumber
                }

                is Resource.Error -> {
                    alertDialog.dismiss()
                    Snackbar.make(
                        binding.root, resource.t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun initViews() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
    }

    // R.id.action_edit_info -> {
    //                val editProfileIntent = Intent(this, ChangeProfileActivity::class.java)
    //                val user = UserInfo(
    //                    binding.tvId.text.toString(),
    //                    binding.tvEmail.text.toString(),
    //                    binding.tvFullName.text.toString(),
    //                    binding.tvPhoneNumber.text.toString()
    //                )
    //                editProfileIntent.putExtra(USER, user)
    //                launcher.launch(editProfileIntent)
    //            }
    //
    //            else -> {
    //                onBackPressed()
    //            }

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