package com.lttrung.notepro.ui.viewprofile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.database.data.networks.models.User
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
class ViewProfileViewModel @Inject constructor(
    private val useCase: ViewProfileUseCase
) : ViewModel() {
    val profile: MutableLiveData<Resource<User>> by lazy {
        MutableLiveData<Resource<User>>()
    }

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var profileDisposable: Disposable? = null

    private val profileObserver: Consumer<User> by lazy {
        Consumer {
            profile.postValue(Resource.Success(it))
        }
    }

    fun getProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            profile.postValue(Resource.Loading())
            profileDisposable?.let {
                composite.remove(it)
            }
            profileDisposable = useCase.getProfile().observeOn(AndroidSchedulers.mainThread())
                .subscribe(profileObserver, this@ViewProfileViewModel::getProfileError)
            profileDisposable?.let { composite.add(it) }
        }
    }

    private fun getProfileError(t: Throwable) {
        when (t) {
            is ConnectivityException -> {
                profile.postValue(Resource.Error(t.message))
            }
            else -> {
                profile.postValue(Resource.Error(t.message ?: "Unknown error"))
            }
        }
    }
}