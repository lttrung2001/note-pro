package com.lttrung.notepro.ui.incomingcall

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.locals.room.entities.CurrentUser
import com.lttrung.notepro.domain.usecases.GetCurrentUserUseCase
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
class IncomingCallViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    internal val currentUserLiveData: MutableLiveData<Resource<CurrentUser>> by lazy {
        MutableLiveData<Resource<CurrentUser>>()
    }

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var currentUserDisposable: Disposable? = null

    private val currentUserObserver: Consumer<CurrentUser> by lazy {
        Consumer {
            currentUserLiveData.value = Resource.Success(it)
        }
    }

    internal fun getCurrentUser() {
        viewModelScope.launch(Dispatchers.IO) {
            currentUserLiveData.postValue(Resource.Loading())
            currentUserDisposable?.let { composite.remove(it) }
            currentUserDisposable =
                getCurrentUserUseCase.execute().observeOn(AndroidSchedulers.mainThread())
                    .subscribe(currentUserObserver) {
                        currentUserLiveData.postValue(Resource.Error(it))
                    }
            currentUserDisposable?.let { composite.add(it) }
        }
    }
}