package com.lttrung.notepro.ui.activities.viewmembers

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityShowMembersBinding
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.data.networks.models.Paging
import com.lttrung.notepro.ui.fragments.addmember.AddMemberFragment
import com.lttrung.notepro.ui.base.adapters.member.MemberAdapter
import com.lttrung.notepro.ui.base.adapters.member.MemberListener
import com.lttrung.notepro.ui.chat.ChatSocketService
import com.lttrung.notepro.ui.activities.editmember.EditMemberActivity
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.MEMBER
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.PAGE_LIMIT
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ViewMembersActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityShowMembersBinding.inflate(layoutInflater)
    }
    private val getMembersViewModel: ViewMembersViewModel by viewModels()
    private val memberListener: MemberListener by lazy {
        object : MemberListener {
            override fun onClick(member: Member) {
                if (note.isOwner()) {
                    val editMemberIntent =
                        Intent(this@ViewMembersActivity, EditMemberActivity::class.java)
                    editMemberIntent.putExtra(NOTE, note)
                    editMemberIntent.putExtra(MEMBER, member)
                    launcher.launch(editMemberIntent)
                }
            }
        }
    }
    private val memberAdapter by lazy {
        MemberAdapter(memberListener)
    }
    private val onScrollListener by lazy {
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    getMembersViewModel.getMembers(
                        note.id,
                        getMembersViewModel.page,
                        PAGE_LIMIT
                    )
                }
            }
        }
    }
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ChatSocketService.LocalBinder
            socketService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }
    private val addMemberFragment by lazy {
        AddMemberFragment()
    }
    private val note by lazy {
        intent.getSerializableExtra(NOTE) as Note
    }
    private lateinit var toAddMemberButton: MenuItem
    private lateinit var socketService: ChatSocketService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initListeners()
        initObservers()
        getMembersViewModel.getMembers(
            note.id, getMembersViewModel.page, PAGE_LIMIT
        )
    }

    override fun onStart() {
        super.onStart()
        Intent(this@ViewMembersActivity, ChatSocketService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
    }

    private fun initListeners() {
    }

    private fun initObservers() {
        getMembersViewModel.membersLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    memberAdapter.showLoading()
                    binding.rcvMembers.removeOnScrollListener(onScrollListener)
                }

                is Resource.Success -> {
                    memberAdapter.hideLoading()
                    val paging = resource.data
                    memberAdapter.setPaging(paging)
                    if (paging.hasNextPage) {
                        binding.rcvMembers.addOnScrollListener(onScrollListener)
                    } else {
                        binding.rcvMembers.removeOnScrollListener(onScrollListener)
                    }
                }

                is Resource.Error -> {
                    memberAdapter.hideLoading()
                    Snackbar.make(
                        binding.root, resource.t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                    binding.rcvMembers.removeOnScrollListener(onScrollListener)
                }
            }
        }
    }

    private fun initViews() {
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_add_member) {
            addMemberFragment.show(supportFragmentManager, addMemberFragment.tag)
        } else {
            finish()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val note = intent.getSerializableExtra(NOTE) as Note
        if (note.isOwner()) {
            menuInflater.inflate(R.menu.menu_show_members, menu)
            menu?.let {
                toAddMemberButton = it.getItem(0)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val resultIntent = result.data
                resultIntent?.let { rIntent ->
                    val editedMember =
                        rIntent.getSerializableExtra(AppConstant.EDITED_MEMBER) as Member?
                    val deletedMember =
                        rIntent.getSerializableExtra(AppConstant.DELETED_MEMBER) as Member?
                    val paging = memberAdapter.getPaging()
                    val members = paging.data.toMutableList()
                    handleEditResult(editedMember, paging, members)
                    handleDeleteResult(deletedMember, paging, members)
                }
            }
        }

    private fun handleDeleteResult(
        deletedMember: Member?,
        paging: Paging<Member>,
        members: MutableList<Member>
    ) {
        deletedMember?.let { member ->
            val findingMember = members.find {
                it.id == member.id
            }
            members.remove(findingMember)
            getMembersViewModel.membersLiveData.postValue(
                Resource.Success(
                    Paging(
                        paging.hasPreviousPage,
                        paging.hasNextPage,
                        members
                    )
                )
            )

            val roomId = note.id
            socketService.sendRemoveMemberMessage(roomId, member.email)
        }
    }

    private fun handleEditResult(
        editedMember: Member?,
        paging: Paging<Member>,
        members: MutableList<Member>
    ) {
        editedMember?.let { member ->
            val findingMember = members.find {
                it.id == member.id
            }
            members.remove(findingMember)
            members.add(member)
            getMembersViewModel.membersLiveData.postValue(
                Resource.Success(
                    Paging(
                        paging.hasPreviousPage,
                        paging.hasNextPage,
                        members
                    )
                )
            )
        }
    }

    fun handleAddResult(member: Member) {
        addMemberFragment.dismiss()
        val paging = memberAdapter.getPaging()
        val members = paging.data.toMutableList()
        members.add(member)
        getMembersViewModel.membersLiveData.postValue(
            Resource.Success(
                Paging(
                    paging.hasPreviousPage,
                    paging.hasNextPage,
                    members
                )
            )
        )

        val roomId = note.id
        socketService.sendAddMemberMessage(roomId, member.email)
    }
}