package com.lttrung.notepro.ui.activities.editmember

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.repositories.MemberRepositories
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditMemberViewModel @Inject constructor(
    private val memberRepositories: MemberRepositories
) : ViewModel() {
    internal val memberDetailsLiveData by lazy {
        MutableLiveData<Resource<Member>>()
    }

    internal val editMemberLiveData by lazy {
        MutableLiveData<Resource<Member>>()
    }

    internal val deleteMemberLiveData by lazy {
        MutableLiveData<Resource<Unit>>()
    }

    internal fun editMember(noteId: String, member: Member) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                editMemberLiveData.postValue(Resource.Loading())
                val editMember = memberRepositories.editMember(noteId, member)
                editMemberLiveData.postValue(Resource.Success(editMember))
            } catch (ex: Exception) {
                editMemberLiveData.postValue(Resource.Error(ex))
            }
        }
    }

    internal fun deleteMember(noteId: String, memberId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deleteMemberLiveData.postValue(Resource.Loading())
                val editMember = memberRepositories.deleteMember(noteId, memberId)
                deleteMemberLiveData.postValue(Resource.Success(editMember))
            } catch (ex: Exception) {
                deleteMemberLiveData.postValue(Resource.Error(ex))
            }
        }
    }

    fun getMemberDetails(noteId: String, memberId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                memberDetailsLiveData.postValue(Resource.Loading())
                val editMember = memberRepositories.getMemberDetails(noteId, memberId)
                memberDetailsLiveData.postValue(Resource.Success(editMember))
            } catch (ex: Exception) {
                memberDetailsLiveData.postValue(Resource.Error(ex))
            }
        }
    }
}