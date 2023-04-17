package com.lttrung.notepro.ui.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.usecases.RegisterUseCase
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
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    internal val register: MutableLiveData<Resource<Unit>> by lazy {
        MutableLiveData<Resource<Unit>>()
    }

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var registerDisposable: Disposable? = null

    private val observerRegister: Consumer<Unit> by lazy {
        Consumer<Unit> {
            register.postValue(Resource.Success(it))
        }
    }

    internal fun register(email: String, password: String, fullName: String, phoneNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            register.postValue(Resource.Loading())
            registerDisposable?.let {
                composite.remove(it)
                it.dispose()
            }
            registerDisposable = registerUseCase.execute(email, password, fullName, phoneNumber)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerRegister) {
                    register.postValue(Resource.Error(it))
                }
            registerDisposable?.let { composite.add(it) }
        }
    }
}