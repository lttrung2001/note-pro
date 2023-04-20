package com.lttrung.notepro.ui.changepassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.usecases.ChangePasswordUseCase
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
class ChangePasswordViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {
    internal val changePassword: MutableLiveData<Resource<String>> by lazy {
        MutableLiveData<Resource<String>>()
    }

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var changePasswordDisposable: Disposable? = null

    private val changePasswordObserver: Consumer<String> by lazy {
        Consumer {
            changePassword.postValue(Resource.Success(it))
        }
    }

    internal fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            changePassword.postValue(Resource.Loading())
            changePasswordDisposable?.let {
                composite.remove(it)
            }
            changePasswordDisposable = changePasswordUseCase.execute(oldPassword, newPassword)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                    changePasswordObserver) {
                    changePassword.postValue(Resource.Error(it))
                }
            changePasswordDisposable?.let { composite.add(it) }
        }
    }
}