package com.lttrung.notepro.ui.activities.resetpassword

import androidx.lifecycle.MutableLiveData
import com.lttrung.notepro.domain.repositories.LoginRepositories
import com.lttrung.notepro.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val loginRepositories: LoginRepositories
) : BaseViewModel() {
    internal val resetPasswordLiveData by lazy {
        MutableLiveData<Unit>()
    }

    internal fun resetPassword(code: String, newPassword: String) {
        launch {
            val resetPassword = loginRepositories.resetPassword(code, newPassword)
            resetPasswordLiveData.postValue(resetPassword)
        }
    }
}