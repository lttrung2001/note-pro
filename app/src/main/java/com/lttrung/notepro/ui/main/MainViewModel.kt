package com.lttrung.notepro.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.usecases.GetArchivedNotesUseCase
import com.lttrung.notepro.domain.usecases.GetNotesUseCase
import com.lttrung.notepro.domain.usecases.GetRemovedNotesUseCase
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
class MainViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val getArchivedNotesUseCase: GetArchivedNotesUseCase,
    private val getRemovedNotesUseCase: GetRemovedNotesUseCase
) : ViewModel() {
    init {
        getNotes(GET_CURRENT_NOTES)
    }

    internal val getNotes: MutableLiveData<Resource<List<Note>>> by lazy {
        MutableLiveData<Resource<List<Note>>>()
    }

    private var getNotesDisposable: Disposable? = null

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private val observerGetNotes: Consumer<List<Note>> by lazy {
        Consumer { data ->
            val allNotes = data.sortedByDescending { it.lastModified }
            getNotes.postValue(Resource.Success(allNotes))
        }
    }

    internal fun getNotes(getNotesType: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            getNotes.postValue(Resource.Loading())
            getNotesDisposable?.let {
                composite.remove(it)
            }
            getNotesDisposable = when (getNotesType) {
                GET_ARCHIVED_NOTES -> getArchivedNotesUseCase.execute()
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(observerGetNotes) {
                        getNotes.postValue(Resource.Error(it))
                    }
                GET_REMOVED_NOTES -> getRemovedNotesUseCase.execute()
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(observerGetNotes) {
                        getNotes.postValue(Resource.Error(it))
                    }
                else -> getNotesUseCase.execute()
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(observerGetNotes) {
                        getNotes.postValue(Resource.Error(it))
                    }
            }
            getNotesDisposable?.let { composite.add(it) }
        }
    }

    companion object {
        internal const val GET_CURRENT_NOTES = 1
        internal const val GET_ARCHIVED_NOTES = 2
        internal const val GET_REMOVED_NOTES = 3
    }
}