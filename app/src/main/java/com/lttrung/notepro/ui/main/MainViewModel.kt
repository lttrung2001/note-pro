package com.lttrung.notepro.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.usecases.GetNotesUseCase
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.annotations.jvm.Mutable

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase
) : ViewModel() {

    internal val getNotes: MutableLiveData<Resource<List<Note>>> by lazy {
        MutableLiveData<Resource<List<Note>>>()
    }

    private var getNotesDisposable: Disposable? = null

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private val observerGetNotes: Consumer<List<Note>> by lazy {
        Consumer { data ->
            getNotes.postValue(Resource.Success(data))
        }
    }

    internal fun getNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            getNotes.postValue(Resource.Loading())
            getNotesDisposable?.let {
                composite.remove(it)
            }
            getNotesDisposable = getNotesUseCase.execute().observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerGetNotes) {
                    getNotes.postValue(Resource.Error(it))
                }
            getNotesDisposable?.let { composite.add(it) }
        }
    }

    internal val archivedNotes: MutableLiveData<List<Note>> by lazy {
        MutableLiveData<List<Note>>()
    }
    internal val removedNotes: MutableLiveData<List<Note>> by lazy {
        MutableLiveData<List<Note>>()
    }
}