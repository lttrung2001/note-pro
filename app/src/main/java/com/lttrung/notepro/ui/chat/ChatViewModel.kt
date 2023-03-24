package com.lttrung.notepro.ui.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.database.data.locals.entities.CurrentUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val useCase: ChatUseCase
) : ViewModel() {
    val userLiveData: MutableLiveData<CurrentUser> by lazy {
        val user = useCase.getCurrentUser()
        MutableLiveData<CurrentUser>(user)
    }
}