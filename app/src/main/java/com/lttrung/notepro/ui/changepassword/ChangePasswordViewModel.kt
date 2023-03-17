package com.lttrung.notepro.ui.changepassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.exceptions.ConnectivityException
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
    private val useCase: ChangePasswordUseCase
) : ViewModel() {
    val changePassword: MutableLiveData<Resource<String>> by lazy {
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

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            changePassword.postValue(Resource.Loading())
            changePasswordDisposable?.let {
                composite.remove(it)
            }
            changePasswordDisposable = useCase.changePassword(oldPassword, newPassword)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(
                    changePasswordObserver, this@ChangePasswordViewModel::changePasswordError
                )
            changePasswordDisposable?.let { composite.add(it) }
        }
    }

    private fun changePasswordError(t: Throwable) {
        when (t) {
            is ConnectivityException -> {
                changePassword.postValue(Resource.Error(t.message))
            }
            else -> {
                changePassword.postValue(Resource.Error(t.message ?: "Unknown error"))
            }
        }
    }
}