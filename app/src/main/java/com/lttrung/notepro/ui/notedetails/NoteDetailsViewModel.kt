package com.lttrung.notepro.ui.notedetails

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.database.data.locals.entities.Note
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
class NoteDetailsViewModel @Inject constructor(
    private val useCase: NoteDetailsUseCase
) : ViewModel() {

    val noteDetails: MutableLiveData<Resource<Note>> by lazy {
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

    fun getNoteDetails(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDetails.postValue(Resource.Loading())
            noteDetailsDisposable?.let {
                composite.remove(it)
                it.dispose()
            }
            noteDetailsDisposable =
                useCase.getNoteDetails(note).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(noteDetailsObserver, this@NoteDetailsViewModel::getNoteDetailsError)
            noteDetailsDisposable?.let { composite.add(it) }
        }
    }

    private fun getNoteDetailsError(t: Throwable) {
        when (t) {
            is ConnectivityException -> {
                noteDetails.postValue(Resource.Error(t.message))
            }
            else -> {
                noteDetails.postValue(Resource.Error(t.message ?: "Unknown error"))
            }
        }
    }

    fun updatePin(noteId: String, isPin: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            useCase.updatePin(noteId, isPin).subscribe()
        }
    }
}