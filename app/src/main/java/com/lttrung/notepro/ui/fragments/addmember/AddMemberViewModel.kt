package com.lttrung.notepro.ui.fragments.addmember

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.repositories.MemberRepositories
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMemberViewModel @Inject constructor(
    private val memberRepositories: MemberRepositories
) : ViewModel() {

    internal val memberLiveData by lazy {
        MutableLiveData<Resource<Member>>()
    }

    internal fun addMember(noteId: String, email: String, role: String) {
        viewModelScope.launch {
            try {
                memberLiveData.postValue(Resource.Loading())
                val member = memberRepositories.addMember(noteId, email, role)
                memberLiveData.postValue(Resource.Success(member))
            } catch (ex: Exception) {
                memberLiveData.postValue(Resource.Error(ex))
            }
        }
    }
}