package com.lttrung.notepro.ui.activities.register

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
class RegisterViewModel @Inject constructor(
    private val loginRepositories: LoginRepositories
) : BaseViewModel() {
    internal val registerLiveData by lazy {
        MutableLiveData<Unit>()
    }

    internal fun register(email: String, password: String, fullName: String, phoneNumber: String) {
        launch {
            val register = loginRepositories.register(email, password, fullName, phoneNumber)
            registerLiveData.postValue(register)
        }
    }
}