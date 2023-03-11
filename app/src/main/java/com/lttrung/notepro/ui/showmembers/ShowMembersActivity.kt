package com.lttrung.notepro.ui.showmembers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Member
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.database.data.networks.models.Paging
import com.lttrung.notepro.databinding.ActivityShowMembersBinding
import com.lttrung.notepro.ui.addmember.AddMemberFragment
import com.lttrung.notepro.ui.base.adapters.member.MemberAdapter
import com.lttrung.notepro.ui.base.adapters.member.MemberListener
import com.lttrung.notepro.ui.editmember.EditMemberActivity
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.MEMBER
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.PAGE_LIMIT
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.util.stream.Collectors
import java.util.stream.Collectors.toCollection


@AndroidEntryPoint
class ShowMembersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowMembersBinding
    private val getMembersViewModel: ShowMembersViewModel by viewModels()
    private lateinit var memberAdapter: MemberAdapter
    private lateinit var toAddMemberButton: MenuItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initListeners()
        initAdapters()
        initObservers()
        if (getMembersViewModel.getMembers.value == null) {
            initData()
        }
    }

    private val refreshListener: SwipeRefreshLayout.OnRefreshListener by lazy {
        SwipeRefreshLayout.OnRefreshListener {
            initData()
        }
    }

    private fun initListeners() {
        binding.refreshLayout.setOnRefreshListener(refreshListener)
    }

    private fun initAdapters() {
        memberAdapter = MemberAdapter(memberListener)
        binding.rcvMembers.adapter = memberAdapter
    }

    private fun initObservers() {
        getMembersViewModel.getMembers.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.refreshLayout.isRefreshing = true
                }
                is Resource.Success -> {
                    binding.refreshLayout.isRefreshing = false
                    memberAdapter.submitList(resource.data.data)
                }
                is Resource.Error -> {
                    binding.refreshLayout.isRefreshing = false
                }
            }
        }

        getMembersViewModel.member.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    val currentList = memberAdapter.currentList.toMutableList()
                    currentList.add(0, resource.data)
                    memberAdapter.submitList(currentList)
                }
                is Resource.Error -> {

                }
            }
        }
    }

    private fun initData() {
        val note = intent.getSerializableExtra(NOTE) as Note
        getMembersViewModel.getMembers(
            note.id, memberAdapter.currentList.size / PAGE_LIMIT, PAGE_LIMIT
        )
    }

    private fun initViews() {
        binding = ActivityShowMembersBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_add_member) {
            val addMemberFragment = AddMemberFragment()
            addMemberFragment.show(supportFragmentManager, addMemberFragment.tag)
            true
        } else {
            onBackPressed()
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val note = intent.getSerializableExtra(NOTE) as Note
        if (note.isOwner()) {
            menuInflater.inflate(R.menu.menu_show_members, menu)
            toAddMemberButton = menu?.getItem(0)!!
        }
        return super.onCreateOptionsMenu(menu)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultIntent = result.data
                resultIntent?.let { intent ->
                    val editedMember =
                        intent.getSerializableExtra(AppConstant.EDITED_MEMBER) as Member?
                    val deletedMember =
                        intent.getSerializableExtra(AppConstant.DELETED_MEMBER) as Member?
                    editedMember?.let { member ->
                        val members = memberAdapter.currentList.toMutableList()
                        val oldMember = members.find {
                            it.id == member.id
                        }
                        members.remove(oldMember)
                        members.add(member)
                        memberAdapter.submitList(members)
                        val r = getOldResource()
                        getMembersViewModel.getMembers.postValue(
                            Resource.Success(
                                Paging(
                                    r.hasPreviousPage,
                                    r.hasNextPage,
                                    members
                                )
                            )
                        )
                    }
                    deletedMember?.let { member ->
                        val members = memberAdapter.currentList.toMutableList()
                        members.remove(member)
                        memberAdapter.submitList(members)
                        val r = getOldResource()
                        getMembersViewModel.getMembers.postValue(
                            Resource.Success(
                                Paging(
                                    r.hasPreviousPage,
                                    r.hasNextPage,
                                    members
                                )
                            )
                        )
                    }
                }
            }
        }

    private val memberListener: MemberListener by lazy {
        object : MemberListener {
            override fun onClick(member: Member) {
                val note = intent.getSerializableExtra(NOTE) as Note
                if (note.isOwner()) {
                    val editMemberIntent =
                        Intent(this@ShowMembersActivity, EditMemberActivity::class.java)
                    editMemberIntent.putExtra(NOTE, note)
                    editMemberIntent.putExtra(MEMBER, member)
                    launcher.launch(editMemberIntent)
                } else {

                }
            }
        }
    }

    private fun getOldResource(): Paging<Member> {
        val resource = getMembersViewModel.getMembers.value as Resource.Success<Paging<Member>>
        return resource.data
    }
}