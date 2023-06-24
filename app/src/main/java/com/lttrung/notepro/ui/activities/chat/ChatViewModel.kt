package com.lttrung.notepro.ui.activities.chat

import androidx.lifecycle.MutableLiveData
import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.domain.repositories.MessageRepositories
import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val userRepositories: UserRepositories,
    private val messageRepositories: MessageRepositories
) : BaseViewModel() {
    internal val currentUserLiveData by lazy {
        MutableLiveData<CurrentUser>()
    }

    internal val messagesLiveData by lazy {
        MutableLiveData<List<Message>>()
    }

    internal var page = 0

    internal fun getCurrentUser() {
        launch {
            val changeProfile = userRepositories.getCurrentUser()
            currentUserLiveData.postValue(changeProfile)
        }
    }

    internal fun getMessages(roomId: String, pageIndex: Int, limit: Int) {
        launch {
            val preMessages = messageRepositories.getMessages(roomId, pageIndex, limit)
            val messages = messagesLiveData.value.orEmpty().toMutableList().apply {
                addAll(0, preMessages)
            }
            messagesLiveData.postValue(messages)
        }
    }
}