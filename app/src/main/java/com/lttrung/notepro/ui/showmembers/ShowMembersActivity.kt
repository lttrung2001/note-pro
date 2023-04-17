package com.lttrung.notepro.ui.showmembers

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
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.R
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.data.networks.models.Paging
import com.lttrung.notepro.databinding.ActivityShowMembersBinding
import com.lttrung.notepro.ui.addmember.AddMemberFragment
import com.lttrung.notepro.ui.base.adapters.member.MemberAdapter
import com.lttrung.notepro.ui.base.adapters.member.MemberListener
import com.lttrung.notepro.ui.chat.ChatSocketService
import com.lttrung.notepro.ui.editmember.EditMemberActivity
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.MEMBER
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.PAGE_LIMIT
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ShowMembersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityShowMembersBinding
    private val getMembersViewModel: ShowMembersViewModel by viewModels()
    private lateinit var memberAdapter: MemberAdapter
    private lateinit var toAddMemberButton: MenuItem
    private lateinit var socketService: ChatSocketService
    private val addMemberFragment: AddMemberFragment by lazy {
        AddMemberFragment()
    }
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

    override fun onStart() {
        super.onStart()
        Intent(this@ShowMembersActivity, ChatSocketService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
    }

    private val onScrollListener: RecyclerView.OnScrollListener by lazy {
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val note = intent.getSerializableExtra(NOTE) as Note
                    getMembersViewModel.getMembers(
                        note.id,
                        getMembersViewModel.page,
                        PAGE_LIMIT
                    )
                }
            }
        }
    }

    private fun initListeners() {
    }

    private fun initAdapters() {
        memberAdapter = MemberAdapter(memberListener)
        binding.rcvMembers.adapter = memberAdapter
    }

    private fun initObservers() {
        getMembersViewModel.getMembers.observe(this) { resource ->
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

    private fun initData() {
        val note = intent.getSerializableExtra(NOTE) as Note
        getMembersViewModel.getMembers(
            note.id, getMembersViewModel.page, PAGE_LIMIT
        )
    }

    private fun initViews() {
        binding = ActivityShowMembersBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
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
            toAddMemberButton = menu?.getItem(0)!!
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
                    editedMember?.let { member ->
                        val findingMember = members.find {
                            it.id == member.id
                        }
                        members.remove(findingMember)
                        members.add(member)
                        getMembersViewModel.getMembers.postValue(
                            Resource.Success(
                                Paging(
                                    paging.hasPreviousPage,
                                    paging.hasNextPage,
                                    members
                                )
                            )
                        )
                    }
                    deletedMember?.let { member ->
                        val findingMember = members.find {
                            it.id == member.id
                        }
                        members.remove(findingMember)
                        getMembersViewModel.getMembers.postValue(
                            Resource.Success(
                                Paging(
                                    paging.hasPreviousPage,
                                    paging.hasNextPage,
                                    members
                                )
                            )
                        )

                        val note = intent.getSerializableExtra(NOTE) as Note
                        val roomId = note.id
                        socketService.sendRemoveMemberMessage(roomId, member.email)
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
                }
            }
        }
    }

    fun addMemberResult(member: Member) {
        addMemberFragment.dismiss()
        val paging = memberAdapter.getPaging()
        val members = paging.data.toMutableList()
        members.add(member)
        getMembersViewModel.getMembers.postValue(
            Resource.Success(
                Paging(
                    paging.hasPreviousPage,
                    paging.hasNextPage,
                    members
                )
            )
        )

        val note = intent.getSerializableExtra(NOTE) as Note
        val roomId = note.id
        socketService.sendAddMemberMessage(roomId, member.email)
    }

    fun onAddMemberFragmentDestroyView() {
        addMemberFragment.dismiss()
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ChatSocketService.LocalBinder
            socketService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }
}