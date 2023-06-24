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
    override val viewModel: EditMemberViewModel by viewModels()
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
        viewModel.getMemberDetails(note.id, member.id)
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.editMemberLiveData.observe(this) { editMember ->
            val resultIntent = Intent()
            resultIntent.putExtra(EDITED_MEMBER, editMember)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        viewModel.deleteMemberLiveData.observe(this) {
            val resultIntent = Intent()
            resultIntent.putExtra(DELETED_MEMBER, member)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        viewModel.memberDetailsLiveData.observe(this) { memberDetails ->
            bindMemberDataToViews(memberDetails)
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
        super.initListeners()
        binding.apply {
            btnDeleteMember.setOnClickListener {
                viewModel.deleteMember(note.id, member.id)
            }
            btnEditMember.setOnClickListener {
                val member = getMemberDataFromUi()
                viewModel.editMember(note.id, member)
            }
        }
    }

    override fun initViews() {
        super.initViews()
        binding.apply {
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