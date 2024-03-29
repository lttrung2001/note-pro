package com.lttrung.notepro.ui.activities.chat

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import com.lttrung.notepro.domain.data.locals.models.MediaSelectionLocalsModel
import com.lttrung.notepro.domain.data.networks.models.Image
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.domain.data.networks.models.Paging
import com.lttrung.notepro.domain.repositories.MessageRepositories
import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.ui.base.BaseViewModel
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.MediaUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val userRepositories: UserRepositories,
    private val messageRepositories: MessageRepositories
) : BaseViewModel() {
    internal var page = 0
    internal var imagePage = 0
    internal var videoPage = 0

    val listMessage by lazy {
        mutableListOf<Message>()
    }
    val listImage by lazy {
        mutableListOf<Image>()
    }

    internal val currentUserLiveData by lazy {
        MutableLiveData<CurrentUser>()
    }

    internal val messagesLiveData by lazy {
        MutableLiveData<List<Message>>()
    }

    internal val imagesLiveData by lazy {
        MutableLiveData<Paging<MediaSelectionLocalsModel>>()
    }

    internal val videosLiveData by lazy {
        MutableLiveData<Paging<MediaSelectionLocalsModel>>()
    }

    val uploadLiveData by lazy {
        MutableLiveData<Map<String, String>>()
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
            listMessage.addAll(0, preMessages)
            page++
            messagesLiveData.postValue(preMessages)
        }
    }

    internal fun getImages(context: Context, page: Int, limit: Int) {
        launch {
            val paging = MediaUtils.findImages(context, page, limit)
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

    internal fun getVideos(context: Context, page: Int, limit: Int) {
        launch {
            val paging = MediaUtils.findVideos(context, page, limit)
            val newPaging = Paging(
                paging.hasPreviousPage,
                paging.hasNextPage,
                videosLiveData.value?.data.orEmpty().toMutableList().apply {
                    addAll(paging.data)
                })
            videosLiveData.postValue(newPaging)
            videoPage++
        }
    }

    fun saveUploadResult(map: Map<String, String>) {
        launch {
            uploadLiveData.postValue(map)
        }
    }
}