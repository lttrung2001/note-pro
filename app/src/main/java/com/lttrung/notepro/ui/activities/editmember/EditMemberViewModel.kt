package com.lttrung.notepro.ui.activities.editmember

import androidx.lifecycle.MutableLiveData
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.repositories.MemberRepositories
import com.lttrung.notepro.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditMemberViewModel @Inject constructor(
    private val memberRepositories: MemberRepositories
) : BaseViewModel() {
    internal val memberDetailsLiveData by lazy {
        MutableLiveData<Member>()
    }

    internal val editMemberLiveData by lazy {
        MutableLiveData<Member>()
    }

    internal val deleteMemberLiveData by lazy {
        MutableLiveData<Unit>()
    }

    internal fun editMember(noteId: String, member: Member) {
        launch {
            val editMember = memberRepositories.editMember(noteId, member)
            editMemberLiveData.postValue(editMember)
        }
    }

    internal fun deleteMember(noteId: String, memberId: String) {
        launch {
            val editMember = memberRepositories.deleteMember(noteId, memberId)
            deleteMemberLiveData.postValue(editMember)
        }
    }

    fun getMemberDetails(noteId: String, memberId: String) {
        launch {
            val memberDetails = memberRepositories.getMemberDetails(noteId, memberId)
            memberDetailsLiveData.postValue(memberDetails)
        }
    }
}