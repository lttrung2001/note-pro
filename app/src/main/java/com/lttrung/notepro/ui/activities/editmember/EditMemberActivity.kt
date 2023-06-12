package com.lttrung.notepro.ui.activities.editmember

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityEditMemberBinding
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.utils.AppConstant.Companion.DELETED_MEMBER
import com.lttrung.notepro.utils.AppConstant.Companion.EDITED_MEMBER
import com.lttrung.notepro.utils.AppConstant.Companion.MEMBER
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditMemberActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityEditMemberBinding.inflate(layoutInflater)
    }
    private val editMemberViewModel: EditMemberViewModel by viewModels()
    private val roleAdapter by lazy {
        ArrayAdapter(
            this@EditMemberActivity,
            android.R.layout.simple_list_item_1,
            listOf("editor", "viewer")
        )
    }
    private val note by lazy {
        intent.getSerializableExtra(NOTE) as Note
    }
    private val member by lazy {
        intent.getSerializableExtra(MEMBER) as Member
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initListeners()
        initObservers()

        editMemberViewModel.getMemberDetails(note.id, member.id)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                val member = Member(
                    binding.tvId.text.toString(),
                    binding.tvEmail.text.toString(),
                    binding.tvFullName.text.toString(),
                    binding.roleSpinner.selectedItem.toString(),
                    binding.tvPhoneNumber.text.toString()
                )
                editMemberViewModel.editMember(note.id, member)
            } else -> {
                finish()
            }
        }
        return true
    }

    private fun initObservers() {
        editMemberViewModel.editMemberLiveData.observe(this) { resource ->
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
                    Toast.makeText(this, resource.t.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }

        editMemberViewModel.deleteMemberLiveData.observe(this) { resource ->
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
                    resultIntent.putExtra(DELETED_MEMBER, member)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }

                is Resource.Error -> {
                    binding.deleteButton.isClickable = true
                    binding.deleteButton.hideProgress(R.string.remove)
                    Toast.makeText(this, resource.t.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }

        editMemberViewModel.memberDetailsLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.deleteButton.isClickable = false
                }

                is Resource.Success -> {
                    binding.deleteButton.isClickable = true
                    val member = resource.data
                    bindMemberDataToViews(member)
                }

                is Resource.Error -> {
                    binding.deleteButton.isClickable = true
                    Toast.makeText(this, resource.t.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun bindMemberDataToViews(member: Member) {
        binding.tvId.text = member.id
        binding.tvEmail.text = member.email
        binding.tvFullName.text = member.fullName
        binding.tvPhoneNumber.text = member.phoneNumber
        binding.roleSpinner.setSelection(roleAdapter.getPosition(member.role))
    }

    private fun initListeners() {
        binding.deleteButton.setOnClickListener {
            editMemberViewModel.deleteMember(note.id, member.id)
        }
    }

    private fun initViews() {
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        bindProgressButton(binding.deleteButton)
        binding.deleteButton.attachTextChangeAnimator()

        binding.roleSpinner.setSelection(roleAdapter.getPosition(member.role))
        binding.tvId.text = member.id
        binding.tvEmail.text = member.email
        binding.tvFullName.text = member.fullName
        binding.tvPhoneNumber.text = member.phoneNumber
    }
}