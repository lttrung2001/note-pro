package com.lttrung.notepro.ui.activities.register

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
class RegisterViewModel @Inject constructor(
    private val loginRepositories: LoginRepositories
) : ViewModel() {
    internal val registerLiveData by lazy {
        MutableLiveData<Resource<Unit>>()
    }

    internal fun register(email: String, password: String, fullName: String, phoneNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            registerLiveData.postValue(Resource.Loading())
            val register = loginRepositories.register(email, password, fullName, phoneNumber)
            registerLiveData.postValue(Resource.Success(register))
        }
    }
}