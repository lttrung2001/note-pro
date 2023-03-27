package com.lttrung.notepro.ui.changeprofile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.database.data.networks.models.UserInfo
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
class ChangeProfileViewModel @Inject constructor(
    private val useCase: ChangeProfileUseCase
) : ViewModel() {
    val changeProfile: MutableLiveData<Resource<UserInfo>> by lazy {
        MutableLiveData<Resource<UserInfo>>()
    }

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var changeProfileDisposable: Disposable? = null

    private val changeProfileObserver: Consumer<UserInfo> by lazy {
        Consumer {
            changeProfile.postValue(Resource.Success(it))
        }
    }

    fun changeProfile(fullName: String, phoneNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            changeProfile.postValue(Resource.Loading())
            changeProfileDisposable?.let {
                composite.remove(it)
            }
            changeProfileDisposable = useCase.changeProfile(fullName, phoneNumber)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(changeProfileObserver, this@ChangeProfileViewModel::changeProfileError)
            changeProfileDisposable?.let { composite.add(it) }
        }
    }

    private fun changeProfileError(t: Throwable) {
        when (t) {
            is ConnectivityException -> {
                changeProfile.postValue(Resource.Error(t.message))
            }
            else -> {
                changeProfile.postValue(Resource.Error(t.message ?: "Unknown error"))
            }
        }
    }
}