package com.lttrung.notepro.ui.activities.viewmembers

import androidx.lifecycle.MutableLiveData
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Paging
import com.lttrung.notepro.domain.repositories.MemberRepositories
import com.lttrung.notepro.ui.base.BaseViewModel
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ViewMembersViewModel @Inject constructor(
    private val memberRepositories: MemberRepositories
) : BaseViewModel() {
    internal var page = 0

    internal val membersLiveData by lazy {
        MutableLiveData<Paging<Member>>()
    }

    internal val addMemberLiveData by lazy {
        MutableLiveData<Member>()
    }

    internal fun getMembers(noteId: String, pageIndex: Int, limit: Int) {
        launch {
            val pagingMember = memberRepositories.getMembers(noteId, pageIndex, limit)
            page++

            if (membersLiveData.value == null || membersLiveData.value!!.data.isEmpty()) {
                membersLiveData.postValue(pagingMember)
            } else {
                val members = membersLiveData.value!!.data
                val allMember = members.toMutableList().apply {
                    addAll(pagingMember.data)
                }
                membersLiveData.postValue(
                    Paging(
                        pagingMember.hasPreviousPage,
                        pagingMember.hasNextPage,
                        allMember
                    )
                )
            }
        }
    }

    internal fun addMember(noteId: String, email: String, role: String) {
        launch {
            val addMember = memberRepositories.addMember(noteId, email, role)
            addMemberLiveData.postValue(addMember)
        }
    }
}