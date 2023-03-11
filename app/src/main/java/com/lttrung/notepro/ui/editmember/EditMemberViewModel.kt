package com.lttrung.notepro.ui.editmember

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.database.data.networks.models.Member
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
class EditMemberViewModel @Inject constructor(
    private val useCase: EditMemberUseCase
) : ViewModel() {
    val memberLiveData: MutableLiveData<Resource<Member>> by lazy {
        MutableLiveData<Resource<Member>>()
    }

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var memberDisposable: Disposable? = null

    private val memberObserver: Consumer<Member> by lazy {
        Consumer {
            memberLiveData.postValue(Resource.Success(it))
        }
    }

    fun editMember(noteId: String, member: Member) {
        viewModelScope.launch(Dispatchers.IO) {
            memberLiveData.postValue(Resource.Loading())
            memberDisposable?.let { composite.remove(it) }
            memberDisposable =
                useCase.editMember(noteId, member).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(memberObserver, this@EditMemberViewModel::editMemberError)
            memberDisposable?.let { composite.add(it) }
        }
    }

    private fun editMemberError(t: Throwable) {
        memberLiveData.postValue(Resource.Error(t.message ?: "Unknown error"))
    }

    val deleteMember: MutableLiveData<Resource<Unit>> by lazy {
        MutableLiveData<Resource<Unit>>()
    }

    private var deleteMemberDisposable: Disposable? = null

    private val deleteMemberObserver: Consumer<Unit> by lazy {
        Consumer {
            deleteMember.postValue(Resource.Success(it))
        }
    }

    fun deleteMember(noteId: String, memberId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteMember.postValue(Resource.Loading())
            deleteMemberDisposable?.let {
                composite.remove(it)
            }
            deleteMemberDisposable =
                useCase.deleteMember(noteId, memberId).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(deleteMemberObserver, this@EditMemberViewModel::deleteMemberError)
            deleteMemberDisposable?.let { composite.add(it) }
        }
    }

    private fun deleteMemberError(t: Throwable) {
        deleteMember.postValue(Resource.Error(t.message ?: "Unknown error"))
    }

    val memberDetails: MutableLiveData<Resource<Member>> by lazy {
        MutableLiveData<Resource<Member>>()
    }

    private var memberDetailsDisposable: Disposable? = null

    private val memberDetailsObserver: Consumer<Member> by lazy {
        Consumer {
            memberDetails.postValue(Resource.Success(it))
        }
    }

    fun getMemberDetails(noteId: String, memberId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            memberDetails.postValue(Resource.Loading())
            memberDetailsDisposable?.let {
                composite.remove(it)
            }
            memberDetailsDisposable =
                useCase.getMemberDetails(noteId, memberId).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        memberDetailsObserver,
                        this@EditMemberViewModel::getMemberDetailsError
                    )
            memberDetailsDisposable?.let { composite.add(it) }
        }
    }

    private fun getMemberDetailsError(t: Throwable) {
        t.printStackTrace()
        memberDetails.postValue(Resource.Error(t.message ?: "Unknown error"))
    }
}