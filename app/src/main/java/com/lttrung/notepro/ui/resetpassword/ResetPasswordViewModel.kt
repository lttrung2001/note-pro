package com.lttrung.notepro.ui.resetpassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.usecases.ResetPasswordUseCase
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
class ResetPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {
    internal val resetPassword: MutableLiveData<Resource<Unit>> by lazy {
        MutableLiveData<Resource<Unit>>()
    }

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var resetPasswordDisposable: Disposable? = null

    private val resetPasswordObserver: Consumer<Unit> by lazy {
        Consumer {
            resetPassword.postValue(Resource.Success(it))
        }
    }

    internal fun resetPassword(code: String, newPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // post value loading
            resetPassword.postValue(Resource.Loading())
            resetPasswordDisposable?.let {
                composite.remove(it)
            }
            resetPasswordDisposable = resetPasswordUseCase.execute(code, newPassword)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resetPasswordObserver) {
                    resetPassword.postValue(Resource.Error(it))
                }
            resetPasswordDisposable?.let { composite.add(it) }
        }
    }
}