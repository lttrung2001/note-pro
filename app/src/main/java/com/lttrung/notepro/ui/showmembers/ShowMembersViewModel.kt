package com.lttrung.notepro.ui.showmembers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.database.data.networks.models.Member
import com.lttrung.notepro.database.data.networks.models.Paging
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
class ShowMembersViewModel @Inject constructor(
    private val useCase: ShowMembersUseCase
) : ViewModel() {
    val getMembers: MutableLiveData<Resource<Paging<Member>>> by lazy {
        MutableLiveData<Resource<Paging<Member>>>()
    }

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var getMembersDisposable: Disposable? = null

    private val getMembersObserver: Consumer<Paging<Member>> by lazy {
        Consumer {
            getMembers.postValue(Resource.Success(it))
        }
    }

    fun getMembers(noteId: String, pageIndex: Int, limit: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            getMembers.postValue(Resource.Loading())
            getMembersDisposable?.let {
                composite.remove(it)
                it.dispose()
            }
            getMembersDisposable = useCase.getMembers(noteId, pageIndex, limit)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(getMembersObserver) { t ->
                    when (t) {
                        is ConnectivityException -> {
                            getMembers.postValue(Resource.Error(t.message))
                        }
                        else -> {
                            getMembers.postValue(Resource.Error(t.message ?: "Unknown error"))
                        }
                    }
                }
            getMembersDisposable?.let { composite.add(it) }
        }
    }
}