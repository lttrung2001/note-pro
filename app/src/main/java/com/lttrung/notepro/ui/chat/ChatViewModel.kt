package com.lttrung.notepro.ui.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lttrung.notepro.database.data.locals.entities.CurrentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    useCase: ChatUseCase
) : ViewModel() {
    val userIdLiveData: MutableLiveData<String> by lazy {
        val id = useCase.getCurrentUserId()
        MutableLiveData<String>(id)
    }
}