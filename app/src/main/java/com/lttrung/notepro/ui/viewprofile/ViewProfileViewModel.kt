package com.lttrung.notepro.ui.viewprofile

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
class ViewProfileViewModel @Inject constructor(
    private val userRepositories: UserRepositories
) : ViewModel() {
    internal val profileLiveData by lazy {
        MutableLiveData<Resource<UserInfo>>()
    }

    internal fun getProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                profileLiveData.postValue(Resource.Loading())
                val userInfo = userRepositories.getProfile()
                profileLiveData.postValue(Resource.Success(userInfo))
            } catch (ex: Exception) {
                profileLiveData.postValue(Resource.Error(ex))
            }
        }
    }
}