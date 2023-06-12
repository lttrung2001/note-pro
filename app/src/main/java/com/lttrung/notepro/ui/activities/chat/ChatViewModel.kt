package com.lttrung.notepro.ui.activities.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val userRepositories: UserRepositories
) : ViewModel() {
    internal val currentUserLiveData by lazy {
        MutableLiveData<Resource<CurrentUser>>()
    }

    internal val messagesLiveData by lazy {
        MutableLiveData<Resource<List<Message>>>()
    }

    internal var page = 0

    internal fun getCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    currentUserLiveData.postValue(Resource.Loading())
                    val changeProfile = userRepositories.getCurrentUser()
                    currentUserLiveData.postValue(Resource.Success(changeProfile))
                } catch (ex: Exception) {
                    currentUserLiveData.postValue(Resource.Error(ex))
                }
            }
        }
    }
}