package com.lttrung.notepro.ui.editmember

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.usecases.DeleteMemberUseCase
import com.lttrung.notepro.domain.usecases.EditMemberUseCase
import com.lttrung.notepro.domain.usecases.GetMemberDetailsUseCase
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
    private val editMemberUseCase: EditMemberUseCase,
    private val deleteMemberUseCase: DeleteMemberUseCase,
    private val getMemberDetailsUseCase: GetMemberDetailsUseCase
) : ViewModel() {
    internal val memberLiveData: MutableLiveData<Resource<Member>> by lazy {
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

    internal fun editMember(noteId: String, member: Member) {
        viewModelScope.launch(Dispatchers.IO) {
            memberLiveData.postValue(Resource.Loading())
            memberDisposable?.let { composite.remove(it) }
            memberDisposable =
                editMemberUseCase.execute(noteId, member).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(memberObserver) {
                        memberLiveData.postValue(Resource.Error(it))
                    }
            memberDisposable?.let { composite.add(it) }
        }
    }

    internal val deleteMember: MutableLiveData<Resource<Unit>> by lazy {
        MutableLiveData<Resource<Unit>>()
    }

    private var deleteMemberDisposable: Disposable? = null

    private val deleteMemberObserver: Consumer<Unit> by lazy {
        Consumer {
            deleteMember.postValue(Resource.Success(it))
        }
    }

    internal fun deleteMember(noteId: String, memberId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteMember.postValue(Resource.Loading())
            deleteMemberDisposable?.let {
                composite.remove(it)
            }
            deleteMemberDisposable =
                deleteMemberUseCase.execute(noteId, memberId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(deleteMemberObserver) {
                        deleteMember.postValue(Resource.Error(it))
                    }
            deleteMemberDisposable?.let { composite.add(it) }
        }
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
                getMemberDetailsUseCase.execute(noteId, memberId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(memberDetailsObserver) {
                        memberDetails.postValue(Resource.Error(it))
                    }
            memberDetailsDisposable?.let { composite.add(it) }
        }
    }
}