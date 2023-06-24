package com.lttrung.notepro.ui.activities.incomingcall

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.ui.base.BaseViewModel
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomingCallViewModel @Inject constructor(
    private val userRepositories: UserRepositories
) : BaseViewModel() {
    internal val currentUserLiveData by lazy {
        MutableLiveData<CurrentUser>()
    }

    internal fun getCurrentUser() {
        launch {
            val user = userRepositories.getCurrentUser()
            currentUserLiveData.postValue(user)
        }
    }
}