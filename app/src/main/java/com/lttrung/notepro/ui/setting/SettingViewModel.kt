package com.lttrung.notepro.ui.setting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import com.lttrung.notepro.domain.repositories.UserRepositories
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userRepositories: UserRepositories
) : ViewModel() {
    internal val userLiveData by lazy {
        MutableLiveData<CurrentUser>()
    }

    internal fun getCurrentUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = userRepositories.getCurrentUser()
            userLiveData.postValue(user)
        }
    }

    internal fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepositories.logout()
        }
    }
}