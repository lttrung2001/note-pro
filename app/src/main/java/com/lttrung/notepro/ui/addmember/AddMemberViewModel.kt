package com.lttrung.notepro.ui.addmember

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.usecases.AddMemberUseCase
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
class AddMemberViewModel @Inject constructor(
    private val addMemberUseCase: AddMemberUseCase
) : ViewModel() {

    internal val member: MutableLiveData<Resource<Member>> by lazy {
        MutableLiveData<Resource<Member>>()
    }

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var memberDisposable: Disposable? = null

    private val memberObserver: Consumer<Member> by lazy {
        Consumer {
            member.postValue(Resource.Success(it))
        }
    }

    internal fun addMember(noteId: String, email: String, role: String) {
        viewModelScope.launch(Dispatchers.IO) {
            member.postValue(Resource.Loading())
            memberDisposable?.let {
                composite.remove(it)
            }
            memberDisposable =
                addMemberUseCase.execute(noteId, email, role)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(memberObserver) {
                        member.postValue(Resource.Error(it))
                    }
            memberDisposable?.let { composite.add(it) }
        }
    }
}