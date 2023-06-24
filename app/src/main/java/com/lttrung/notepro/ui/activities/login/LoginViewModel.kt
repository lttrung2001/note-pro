package com.lttrung.notepro.ui.activities.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.repositories.LoginRepositories
import com.lttrung.notepro.ui.base.BaseViewModel
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepositories: LoginRepositories
) : BaseViewModel() {
    internal val refreshTokenLiveData by lazy {
        MutableLiveData<String>()
    }

    internal fun login(email: String, password: String) {
        launch {
            val refreshToken = loginRepositories.login(email, password)
            refreshTokenLiveData.postValue(refreshToken)
        }
    }
}