package com.lttrung.notepro.ui.resetpassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.repositories.LoginRepositories
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val loginRepositories: LoginRepositories
) : ViewModel() {
    internal val resetPasswordLiveData by lazy {
        MutableLiveData<Resource<Unit>>()
    }

    internal fun resetPassword(code: String, newPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                resetPasswordLiveData.postValue(Resource.Loading())
                val resetPassword = loginRepositories.resetPassword(code, newPassword)
                resetPasswordLiveData.postValue(Resource.Success(resetPassword))
            } catch (ex: Exception) {
                resetPasswordLiveData.postValue(Resource.Error(ex))
            }
        }
    }
}