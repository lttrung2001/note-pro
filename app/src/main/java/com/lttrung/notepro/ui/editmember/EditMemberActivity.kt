package com.lttrung.notepro.ui.editmember

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Member
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.databinding.ActivityEditMemberBinding
import com.lttrung.notepro.utils.AppConstant.Companion.DELETED_MEMBER
import com.lttrung.notepro.utils.AppConstant.Companion.EDITED_MEMBER
import com.lttrung.notepro.utils.AppConstant.Companion.MEMBER
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditMemberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditMemberBinding
    private lateinit var roleAdapter: ArrayAdapter<String>
    private val editMemberViewModel: EditMemberViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initListeners()
        initObservers()
        if (editMemberViewModel.memberDetails.value == null) {
            val note = intent.getSerializableExtra(NOTE) as Note
            val member = intent.getSerializableExtra(MEMBER) as Member
            editMemberViewModel.getMemberDetails(note.id, member.id)
        }
    }

    private fun initObservers() {
        editMemberViewModel.memberLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    val resultIntent = Intent()
                    resultIntent.putExtra(EDITED_MEMBER, resource.data)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
                is Resource.Error -> {
                    Log.e("ERROR", resource.message)
                }
            }
        }

        editMemberViewModel.deleteMember.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.deleteButton.isClickable = false
                    binding.deleteButton.showProgress {
                        buttonTextRes = R.string.loading
                    }
                }
                is Resource.Success -> {
                    binding.deleteButton.isClickable = true
                    binding.deleteButton.hideProgress(R.string.remove)
                    val resultIntent = Intent()
                    val member = intent.getSerializableExtra(MEMBER) as Member
                    resultIntent.putExtra(DELETED_MEMBER, member)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
                is Resource.Error -> {
                    binding.deleteButton.isClickable = true
                    binding.deleteButton.hideProgress(R.string.remove)
                    Log.e("ERROR", resource.message)
                    Snackbar.make(binding.root, resource.message,
                        BaseTransientBottomBar.LENGTH_LONG
                    ).show()
                }
            }
        }

        editMemberViewModel.memberDetails.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
//                    binding.deleteButton.isClickable = false
                }
                is Resource.Success -> {
//                    binding.deleteButton.isClickable = true
                    val member = resource.data
                    binding.tvId.text = member.id
                    binding.tvEmail.text = member.email
                    binding.tvFullName.text = member.fullName
                    binding.tvPhoneNumber.text = member.phoneNumber
                    binding.roleSpinner.setSelection(roleAdapter.getPosition(member.role))
                }
                is Resource.Error -> {
//                    binding.deleteButton.isClickable = false
                    Log.e("ERROR", resource.message)
                }
            }
        }
    }

    private fun initListeners() {
        binding.deleteButton.setOnClickListener(deleteListener)
    }

    private fun initViews() {
        binding = ActivityEditMemberBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)

        bindProgressButton(binding.deleteButton)
        binding.deleteButton.attachTextChangeAnimator()

        val member = intent.getSerializableExtra(MEMBER) as Member

        val roles = arrayListOf("editor", "viewer")
        roleAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, roles)
        binding.roleSpinner.adapter = roleAdapter

        binding.roleSpinner.setSelection(roleAdapter.getPosition(member.role))
        binding.tvId.text = member.id
        binding.tvEmail.text = member.email
        binding.tvFullName.text = member.fullName
        binding.tvPhoneNumber.text = member.phoneNumber
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                val note = intent.getSerializableExtra(NOTE) as Note
                val member = Member(
                    binding.tvId.text.toString(),
                    binding.tvEmail.text.toString(),
                    binding.tvFullName.text.toString(),
                    binding.roleSpinner.selectedItem.toString(),
                    binding.tvPhoneNumber.text.toString()
                )
                editMemberViewModel.editMember(note.id, member)
            }
            else -> {
                onBackPressed()
            }
        }
        return true
    }

    private val deleteListener: View.OnClickListener by lazy {
        View.OnClickListener {
            val note = intent.getSerializableExtra(NOTE) as Note
            val member = intent.getSerializableExtra(MEMBER) as Member
            editMemberViewModel.deleteMember(note.id, member.id)
        }
    }
}