package com.lttrung.notepro.ui.viewmembers

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Paging
import com.lttrung.notepro.domain.usecases.ViewMembersUseCase
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
class ViewMembersViewModel @Inject constructor(
    private val viewMembersUseCase: ViewMembersUseCase
) : ViewModel() {
    internal var page = 0

    internal val getMembers: MutableLiveData<Resource<Paging<Member>>> by lazy {
        MutableLiveData<Resource<Paging<Member>>>()
    }

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var getMembersDisposable: Disposable? = null

    private val getMembersObserver: Consumer<Paging<Member>> by lazy {
        Consumer {
            page++
            val oldResource = getMembers.value
            if (oldResource is Resource.Loading<Paging<Member>>) {
                val members = oldResource.data?.data?.toMutableList()
                members?.addAll(it.data)
                val newPaging = Paging(it.hasPreviousPage, it.hasNextPage, members ?: it.data)
                getMembers.postValue(Resource.Success(newPaging))
            }
        }
    }

    internal fun getMembers(noteId: String, pageIndex: Int, limit: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val value = getMembers.value
            if (value is Resource.Success<Paging<Member>>) {
                val oldPaging = value.data
                getMembers.postValue(Resource.Loading(oldPaging))
            } else {
                getMembers.postValue(Resource.Loading())
            }

            getMembersDisposable?.let {
                composite.remove(it)
                it.dispose()
            }
            getMembersDisposable = viewMembersUseCase.execute(noteId, pageIndex, limit)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(getMembersObserver) {
                    getMembers.postValue(Resource.Error(it))
                }
            getMembersDisposable?.let { composite.add(it) }
        }
    }
}