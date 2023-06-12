package com.lttrung.notepro.ui.activities.changepassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val userRepositories: UserRepositories
) : ViewModel() {
    internal val changePasswordLiveData by lazy {
        MutableLiveData<Resource<String>>()
    }

    internal fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                changePasswordLiveData.postValue(Resource.Loading())
                val changePassword = userRepositories.changePassword(oldPassword, newPassword)
                changePasswordLiveData.postValue(Resource.Success(changePassword))
            } catch (ex: Exception) {
                changePasswordLiveData.postValue(Resource.Error(ex))
            }
        }
    }
}