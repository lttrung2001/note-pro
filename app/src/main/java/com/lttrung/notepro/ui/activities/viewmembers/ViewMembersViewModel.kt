package com.lttrung.notepro.ui.activities.viewmembers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Paging
import com.lttrung.notepro.domain.repositories.MemberRepositories
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewMembersViewModel @Inject constructor(
    private val memberRepositories: MemberRepositories
) : ViewModel() {
    internal var page = 0

    internal val membersLiveData by lazy {
        MutableLiveData<Resource<Paging<Member>>>()
    }

    internal fun getMembers(noteId: String, pageIndex: Int, limit: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                membersLiveData.postValue(Resource.Loading())
                val pagingMember = memberRepositories.getMembers(noteId, pageIndex, limit)
                page++
                val value = membersLiveData.value
                if (value is Resource.Loading || value is Resource.Error) {
                    membersLiveData.postValue(Resource.Success(pagingMember))
                } else {
                    value as Resource.Success
                    val totalMember = value.data.data.toMutableList()
                    totalMember.addAll(pagingMember.data)
                    membersLiveData.postValue(
                        Resource.Success(
                            Paging(
                                pagingMember.hasPreviousPage,
                                pagingMember.hasNextPage,
                                totalMember
                            )
                        )
                    )
                }
            } catch (ex: Exception) {
                membersLiveData.postValue(Resource.Error(ex))
            }
        }
    }
}