package com.lttrung.notepro.ui.activities.changeprofile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.networks.models.UserInfo
import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangeProfileViewModel @Inject constructor(
    private val userRepositories: UserRepositories
) : ViewModel() {
    internal val changeProfileLiveData by lazy {
        MutableLiveData<Resource<UserInfo>>()
    }

    internal fun changeProfile(fullName: String, phoneNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                changeProfileLiveData.postValue(Resource.Loading())
                val changeProfile = userRepositories.changeProfile(fullName, phoneNumber)
                changeProfileLiveData.postValue(Resource.Success(changeProfile))
            } catch (ex: Exception) {
                changeProfileLiveData.postValue(Resource.Error(ex))
            }
        }
    }
}