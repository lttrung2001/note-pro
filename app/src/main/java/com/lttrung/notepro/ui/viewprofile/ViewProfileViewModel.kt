package com.lttrung.notepro.ui.viewprofile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.networks.models.UserInfo
import com.lttrung.notepro.domain.usecases.ViewProfileUseCase
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
class ViewProfileViewModel @Inject constructor(
    private val viewProfileUseCase: ViewProfileUseCase
) : ViewModel() {
    internal val profile: MutableLiveData<Resource<UserInfo>> by lazy {
        MutableLiveData<Resource<UserInfo>>()
    }

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var profileDisposable: Disposable? = null

    private val profileObserver: Consumer<UserInfo> by lazy {
        Consumer {
            profile.postValue(Resource.Success(it))
        }
    }

    internal fun getProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            profile.postValue(Resource.Loading())
            profileDisposable?.let {
                composite.remove(it)
            }
            profileDisposable =
                viewProfileUseCase.execute().observeOn(AndroidSchedulers.mainThread())
                    .subscribe(profileObserver) {
                        profile.postValue(Resource.Error(it))
                    }
            profileDisposable?.let { composite.add(it) }
        }
    }
}