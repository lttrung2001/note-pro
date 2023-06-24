package com.lttrung.notepro.ui.activities.changepassword

import androidx.lifecycle.MutableLiveData
import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val userRepositories: UserRepositories
) : BaseViewModel() {
    internal val changePasswordLiveData by lazy {
        MutableLiveData<String>()
    }

    internal fun changePassword(oldPassword: String, newPassword: String) {
        launch {
            val changePassword = userRepositories.changePassword(oldPassword, newPassword)
            changePasswordLiveData.postValue(changePassword)
        }
    }
}