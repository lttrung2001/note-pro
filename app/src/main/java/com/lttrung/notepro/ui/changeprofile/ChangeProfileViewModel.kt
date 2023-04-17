package com.lttrung.notepro.ui.changeprofile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.networks.models.UserInfo
import com.lttrung.notepro.domain.usecases.ChangeProfileUseCase
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
    private val changeProfileUseCase: ChangeProfileUseCase
) : ViewModel() {
    internal val changeProfile: MutableLiveData<Resource<UserInfo>> by lazy {
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

    internal fun changeProfile(fullName: String, phoneNumber: String) {
        viewModelScope.launch(Dispatchers.IO) {
            changeProfile.postValue(Resource.Loading())
            changeProfileDisposable?.let {
                composite.remove(it)
            }
            changeProfileDisposable = changeProfileUseCase.execute(fullName, phoneNumber)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(changeProfileObserver) {
                    changeProfile.postValue(Resource.Error(it))
                }
            changeProfileDisposable?.let { composite.add(it) }
        }
    }
}