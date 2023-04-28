package com.lttrung.notepro.ui.notedetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.usecases.EditNoteUseCase
import com.lttrung.notepro.domain.usecases.NoteDetailsUseCase
import com.lttrung.notepro.domain.usecases.UpdatePinStatusUseCase
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
class NoteDetailsViewModel @Inject constructor(
    private val noteDetailsUseCase: NoteDetailsUseCase,
    private val editNoteUseCase: EditNoteUseCase
) : ViewModel() {

    internal val noteDetails: MutableLiveData<Resource<Note>> by lazy {
        MutableLiveData<Resource<Note>>()
    }

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var noteDetailsDisposable: Disposable? = null

    private val noteDetailsObserver: Consumer<Note> by lazy {
        Consumer {
            noteDetails.postValue(Resource.Success(it))
        }
    }

    internal fun getNoteDetails(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDetails.postValue(Resource.Loading())
            noteDetailsDisposable?.let {
                composite.remove(it)
                it.dispose()
            }
            noteDetailsDisposable =
                noteDetailsUseCase.execute(note).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(noteDetailsObserver) {
                        noteDetails.postValue(Resource.Error(it))
                    }
            noteDetailsDisposable?.let { composite.add(it) }
        }
    }


    internal fun editNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            editNoteUseCase.execute(note, emptyList()).subscribe()
        }
    }
}