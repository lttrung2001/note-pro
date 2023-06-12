package com.lttrung.notepro.ui.activities.forgotpassword

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
class ForgotPasswordViewModel @Inject constructor(
    private val loginRepositories: LoginRepositories
) : ViewModel() {
    internal val forgotPasswordLiveData by lazy {
        MutableLiveData<Resource<Unit>>()
    }

    internal fun forgotPassword(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                forgotPasswordLiveData.postValue(Resource.Loading())
                val forgotPassword = loginRepositories.forgotPassword(email)
                forgotPasswordLiveData.postValue(Resource.Success(forgotPassword))
            } catch (ex: Exception) {
                forgotPasswordLiveData.postValue(Resource.Error(ex))
            }
        }
    }
}