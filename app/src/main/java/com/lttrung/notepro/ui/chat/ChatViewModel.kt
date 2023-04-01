package com.lttrung.notepro.ui.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.database.data.locals.entities.CurrentUser
import com.lttrung.notepro.database.data.networks.models.Message
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val useCase: ChatUseCase
) : ViewModel() {
    val currentUserLiveData: MutableLiveData<Resource<CurrentUser>> by lazy {
        MutableLiveData<Resource<CurrentUser>>()
    }

    val messagesLiveData: MutableLiveData<Resource<List<Message>>> by lazy {
        MutableLiveData<Resource<List<Message>>>()
    }

    var page = 0

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var currentUserDisposable: Disposable? = null

    private val currentUserObserver: Consumer<CurrentUser> by lazy {
        Consumer {
            currentUserLiveData.value = Resource.Success(it)
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            currentUserLiveData.postValue(Resource.Loading())
            currentUserDisposable?.let { composite.remove(it) }
            currentUserDisposable =
                useCase.getCurrentUser().observeOn(AndroidSchedulers.mainThread())
                    .subscribe(currentUserObserver, this@ChatViewModel::getCurrentUserError)
            currentUserDisposable?.let { composite.add(it) }
        }
    }

    private fun getCurrentUserError(t: Throwable) {
        currentUserLiveData.postValue(Resource.Error(t.message ?: "Unknown error"))
    }
}