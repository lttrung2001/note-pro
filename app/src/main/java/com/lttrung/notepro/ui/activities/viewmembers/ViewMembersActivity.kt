package com.lttrung.notepro.ui.activities.viewmembers

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager.LayoutParams
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.lttrung.notepro.databinding.ActivityViewMembersBinding
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.data.networks.models.Paging
import com.lttrung.notepro.ui.activities.chat.ChatSocketService
import com.lttrung.notepro.ui.activities.editmember.EditMemberActivity
import com.lttrung.notepro.ui.adapters.MemberAdapter
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.ui.dialogs.AddMemberDialog
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.MEMBER
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.PAGE_LIMIT
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ViewMembersActivity : BaseActivity() {
    override val binding by lazy {
        ActivityViewMembersBinding.inflate(layoutInflater)
    }
    override val viewModel: ViewMembersViewModel by viewModels()
    private val memberAdapter: MemberAdapter by lazy {
        MemberAdapter(object : MemberAdapter.MemberListener {
            override fun onClick(member: Member) {
                if (note.isOwner()) {
                    val editMemberIntent =
                        Intent(this@ViewMembersActivity, EditMemberActivity::class.java)
                    editMemberIntent.putExtra(NOTE, note)
                    editMemberIntent.putExtra(MEMBER, member)
                    launcher.launch(editMemberIntent)
                }
            }
        })
    }
    private val onScrollListener by lazy {
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    viewModel.getMembers(
                        note.id, viewModel.page, PAGE_LIMIT
                    )
                }
            }
        }
    }

    private lateinit var addMemberDialog: AddMemberDialog

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ChatSocketService.LocalBinder
            socketService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    private val note by lazy {
        intent.getSerializableExtra(NOTE) as Note
    }
    private lateinit var socketService: ChatSocketService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initListeners()
        initObservers()
        viewModel.getMembers(
            note.id, viewModel.page, PAGE_LIMIT
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

    override fun initListeners() {
        super.initListeners()
        binding.fabAddMember.setOnClickListener {
            addMemberDialog = AddMemberDialog(this@ViewMembersActivity) { email, role ->
                viewModel.addMember(note.id, email, role)
            }
            if (!addMemberDialog.isShowing) {
                addMemberDialog.show()
                addMemberDialog.window?.setLayout(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT
                )
            }
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.membersLiveData.observe(this) { paging ->
            memberAdapter.setPaging(paging)
            if (paging.hasNextPage) {
                binding.rvMembers.addOnScrollListener(onScrollListener)
            } else {
                binding.rvMembers.removeOnScrollListener(onScrollListener)
            }
        }

        viewModel.addMemberLiveData.observe(this) { member ->
            handleAddResult(member)
        }
    }

    override fun initViews() {
        super.initViews()
        binding.rvMembers.adapter = memberAdapter
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
        deletedMember: Member?, paging: Paging<Member>, members: MutableList<Member>
    ) {
        deletedMember?.let { member ->
            val findingMember = members.find {
                it.id == member.id
            }
            members.remove(findingMember)
            viewModel.membersLiveData.postValue(
                Paging(
                    paging.hasPreviousPage, paging.hasNextPage, members
                )
            )

            socketService.sendRemoveMemberMessage(note.id, member.email)
        }
    }

    private fun handleEditResult(
        editedMember: Member?, paging: Paging<Member>, members: MutableList<Member>
    ) {
        editedMember?.let { member ->
            val findingMember = members.find {
                it.id == member.id
            }
            members.remove(findingMember)
            members.add(member)
            viewModel.membersLiveData.postValue(
                Paging(
                    paging.hasPreviousPage, paging.hasNextPage, members
                )
            )
        }
    }

    private fun handleAddResult(newMember: Member) {
        val paging = viewModel.membersLiveData.value
        val members = paging!!.data.toMutableList().apply {
            find { it.id == newMember.id } ?: add(newMember)
        }
        viewModel.membersLiveData.postValue(
            Paging(
                paging.hasPreviousPage, paging.hasNextPage, members
            )
        )
        addMemberDialog.dismiss()

        socketService.sendAddMemberMessage(note.id, newMember.email)
    }
}