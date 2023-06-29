package com.lttrung.notepro.ui.activities.chat

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import com.lttrung.notepro.domain.data.locals.models.ImageSelectionLocalsModel
import com.lttrung.notepro.domain.data.networks.models.Image
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.domain.data.networks.models.Paging
import com.lttrung.notepro.domain.repositories.MessageRepositories
import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.ui.base.BaseViewModel
import com.lttrung.notepro.utils.GalleryUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val userRepositories: UserRepositories,
    private val messageRepositories: MessageRepositories
) : BaseViewModel() {
    internal var page = 0
    internal var imagePage = 0

    internal val currentUserLiveData by lazy {
        MutableLiveData<CurrentUser>()
    }

    internal val messagesLiveData by lazy {
        MutableLiveData<List<Message>>()
    }

    internal val imagesLiveData by lazy {
        MutableLiveData<Paging<ImageSelectionLocalsModel>>()
    }

    internal fun getCurrentUser() {
        launch {
            val changeProfile = userRepositories.getCurrentUser()
            currentUserLiveData.postValue(changeProfile)
        }
    }

    internal fun getMessages(roomId: String, pageIndex: Int, limit: Int) {
        launch {
            val preMessages = messageRepositories.getMessages(roomId, pageIndex, limit)
            page++
            val messages = messagesLiveData.value.orEmpty().toMutableList().apply {
                addAll(0, preMessages)
            }
            messagesLiveData.postValue(messages)
        }
    }

    internal fun getImages(context: Context, page: Int, limit: Int) {
        launch {
            val paging = GalleryUtils.findImages(context, page, limit)
            val newPaging = Paging(
                paging.hasPreviousPage,
                paging.hasNextPage,
                imagesLiveData.value?.data.orEmpty().toMutableList().apply {
                    addAll(paging.data)
                })
            imagesLiveData.postValue(newPaging)
            imagePage++
        }
    }
}