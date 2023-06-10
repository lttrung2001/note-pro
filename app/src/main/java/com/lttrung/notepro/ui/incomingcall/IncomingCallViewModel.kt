package com.lttrung.notepro.ui.incomingcall

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomingCallViewModel @Inject constructor(
    private val userRepositories: UserRepositories
) : ViewModel() {
    internal val currentUserLiveData by lazy {
        MutableLiveData<Resource<CurrentUser>>()
    }

    internal fun getCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                currentUserLiveData.postValue(Resource.Loading())
                val user = userRepositories.getCurrentUser()
                currentUserLiveData.postValue(Resource.Success(user))
            } catch (ex: Exception) {
                currentUserLiveData.postValue(Resource.Error(ex))
            }
        }
    }
}