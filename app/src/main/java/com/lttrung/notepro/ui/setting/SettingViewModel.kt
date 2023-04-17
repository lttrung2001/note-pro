package com.lttrung.notepro.ui.setting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import com.lttrung.notepro.domain.usecases.GetCurrentUserUseCase
import com.lttrung.notepro.domain.usecases.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    internal fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            logoutUseCase.execute()
        }
    }

    internal val userLiveData: MutableLiveData<CurrentUser> by lazy {
        MutableLiveData<CurrentUser>()
    }

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var fullNameDisposable: Disposable? = null

    private val fullNameObserver: Consumer<CurrentUser> by lazy {
        Consumer {
            userLiveData.postValue(it)
        }
    }

    internal fun getCurrentUserInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            fullNameDisposable?.let { composite.remove(it) }
            fullNameDisposable =
                getCurrentUserUseCase.execute().observeOn(AndroidSchedulers.mainThread())
                    .subscribe(fullNameObserver)
            fullNameDisposable?.let { composite.add(it) }
        }
    }
}