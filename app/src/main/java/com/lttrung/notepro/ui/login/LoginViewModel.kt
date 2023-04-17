package com.lttrung.notepro.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.usecases.LoginUseCase
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
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    internal val login: MutableLiveData<Resource<String>> by lazy {
        MutableLiveData<Resource<String>>()
    }

    // Quản lý các disposable
    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    // Khai báo disposable login
    private var loginDisposable: Disposable? = null

    // observer (dùng để đăng ký subcribe)
    private val observerLogin: Consumer<String> by lazy {
        Consumer<String> {
            login.postValue(Resource.Success(it))
        }
    }

    internal fun login(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // post value loading
            login.postValue(Resource.Loading())
            loginDisposable?.let {
                composite.remove(it)
                it.dispose()
            }
            loginDisposable = loginUseCase.execute(email, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerLogin) {
                    login.postValue(Resource.Error(it))
                }
            loginDisposable?.let { composite.add(it) }
        }
    }
}