package com.lttrung.notepro.ui.activities.editmember

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityEditMemberBinding
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.utils.AppConstant.Companion.DELETED_MEMBER
import com.lttrung.notepro.utils.AppConstant.Companion.EDITED_MEMBER
import com.lttrung.notepro.utils.AppConstant.Companion.MEMBER
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditMemberActivity : BaseActivity() {
    override val binding by lazy {
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

    override fun initObservers() {
        editMemberViewModel.editMemberLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnEditMember.apply {
                        isClickable = false
                        showProgress {
                            buttonTextRes = R.string.loading
                        }
                    }
                }

                is Resource.Success -> {
                    binding.btnDeleteMember.apply {
                        isClickable = true
                        hideProgress(R.string.remove)
                    }
                    val resultIntent = Intent()
                    resultIntent.putExtra(EDITED_MEMBER, resource.data)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }

                is Resource.Error -> {
                    binding.btnDeleteMember.apply {
                        isClickable = true
                        hideProgress(R.string.remove)
                    }
                    Toast.makeText(this, resource.t.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }

        editMemberViewModel.deleteMemberLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.btnDeleteMember.apply {
                        isClickable = false
                        showProgress {
                            buttonTextRes = R.string.loading
                        }
                    }
                }

                is Resource.Success -> {
                    binding.btnDeleteMember.apply {
                        isClickable = true
                        hideProgress(R.string.remove)
                    }
                    val resultIntent = Intent()
                    resultIntent.putExtra(DELETED_MEMBER, member)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }

                is Resource.Error -> {
                    binding.btnDeleteMember.apply {
                        isClickable = true
                        hideProgress(R.string.remove)
                    }
                    Toast.makeText(this, resource.t.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }

        editMemberViewModel.memberDetailsLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.apply {
                        btnDeleteMember.isClickable = false
                        btnEditMember.isClickable = false
                    }
                }

                is Resource.Success -> {
                    binding.apply {
                        btnDeleteMember.isClickable = true
                        btnEditMember.isClickable = true
                    }
                    val member = resource.data
                    bindMemberDataToViews(member)
                }

                is Resource.Error -> {
                    binding.apply {
                        btnDeleteMember.isClickable = true
                        btnEditMember.isClickable = true
                    }
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

    private fun getMemberDataFromUi(): Member {
        return Member(
            binding.tvId.text.toString(),
            binding.tvEmail.text.toString(),
            binding.tvFullName.text.toString(),
            binding.roleSpinner.selectedItem.toString(),
            binding.tvPhoneNumber.text.toString()
        )
    }

    override fun initListeners() {
        binding.apply {
            btnDeleteMember.setOnClickListener {
                editMemberViewModel.deleteMember(note.id, member.id)
            }
            btnEditMember.setOnClickListener {
                val member = getMemberDataFromUi()
                editMemberViewModel.editMember(note.id, member)
            }
        }
    }

    override fun initViews() {
        setContentView(binding.root)
        bindProgressButton(binding.btnDeleteMember)
        bindProgressButton(binding.btnEditMember)
        binding.apply {
            btnDeleteMember.attachTextChangeAnimator()
            btnEditMember.attachTextChangeAnimator()
            roleSpinner.setSelection(roleAdapter.getPosition(member.role))
            tvId.text = member.id
            tvEmail.text = member.email
            tvFullName.text = member.fullName
            tvPhoneNumber.text = member.phoneNumber
            roleSpinner.apply {
                adapter = roleAdapter
                setSelection(roleAdapter.getPosition(member.role))
            }
        }
    }
}