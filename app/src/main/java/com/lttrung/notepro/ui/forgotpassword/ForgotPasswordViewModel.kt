package com.lttrung.notepro.ui.forgotpassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.usecases.ForgotPasswordUseCase
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
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : ViewModel() {
    internal val forgotPassword: MutableLiveData<Resource<Unit>> by lazy {
        MutableLiveData<Resource<Unit>>()
    }

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var forgotPasswordDisposable: Disposable? = null

    private val forgotPasswordObserver: Consumer<Unit> by lazy {
        Consumer {
            forgotPassword.postValue(Resource.Success(it))
        }
    }

    internal fun forgotPassword(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // post value loading
            forgotPassword.postValue(Resource.Loading())
            forgotPasswordDisposable?.let {
                composite.remove(it)
            }
            forgotPasswordDisposable = forgotPasswordUseCase.execute(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(forgotPasswordObserver) {
                    forgotPassword.postValue(Resource.Error(it))
                }
            forgotPasswordDisposable?.let { composite.add(it) }
        }
    }
}