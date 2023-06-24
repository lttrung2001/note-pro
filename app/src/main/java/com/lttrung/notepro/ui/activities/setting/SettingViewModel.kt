package com.lttrung.notepro.ui.activities.setting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userRepositories: UserRepositories
) : BaseViewModel() {
    internal val userLiveData by lazy {
        MutableLiveData<CurrentUser>()
    }

    internal fun getCurrentUserInfo() {
        launch {
            val user = userRepositories.getCurrentUser()
            userLiveData.postValue(user)
        }
    }

    internal fun logout() {
        launch {
            userRepositories.logout()
        }
    }
}