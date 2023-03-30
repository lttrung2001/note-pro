package com.lttrung.notepro.ui.editnote

import android.webkit.URLUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.database.data.networks.models.Image
import com.lttrung.notepro.database.data.networks.models.Note
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
class EditNoteViewModel @Inject constructor(
    private val useCase: EditNoteUseCase
) : ViewModel() {
    private val deleteImages: MutableLiveData<List<Image>> by lazy {
        MutableLiveData<List<Image>>(emptyList())
    }

    val editNote: MutableLiveData<Resource<Note>> by lazy {
        MutableLiveData<Resource<Note>>()
    }

    val deleteNote: MutableLiveData<Resource<Unit>> by lazy {
        MutableLiveData<Resource<Unit>>()
    }

    val noteDetails: MutableLiveData<Resource<Note>> by lazy {
        MutableLiveData<Resource<Note>>()
    }

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var editNoteDisposable: Disposable? = null
    private val editNoteObserver: Consumer<Note> by lazy {
        Consumer {
            editNote.postValue(Resource.Success(it))
            deleteImages.postValue(emptyList())
        }
    }

    private var deleteNoteDisposable: Disposable? = null
    private val deleteNoteObserver: Consumer<Unit> by lazy {
        Consumer {
            deleteNote.postValue(Resource.Success(it))
        }
    }

    private var noteDetailsDisposable: Disposable? = null
    private val noteDetailsObserver: Consumer<Note> by lazy {
        Consumer {
            noteDetails.postValue(Resource.Success(it))
        }
    }

    fun editNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            editNote.postValue(Resource.Loading())
            editNoteDisposable?.let {
                composite.remove(it)
                it.dispose()
            }
            val deletingImageIds = deleteImages.value!!.map {
                it.id
            }
            editNoteDisposable =
                useCase.editNote(note, deletingImageIds).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(editNoteObserver, this@EditNoteViewModel::editNoteError)
            editNoteDisposable?.let { composite.add(it) }
        }
    }

    private fun editNoteError(t: Throwable) {
        when (t) {
            is ConnectivityException -> {
                editNote.postValue(Resource.Error(t.message))
            }
            else -> {
                editNote.postValue(Resource.Error(t.message ?: "Unknown error"))
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteNote.postValue(Resource.Loading())
            deleteNoteDisposable?.let {
                composite.remove(it)
            }
            deleteNoteDisposable =
                useCase.deleteNote(noteId).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(deleteNoteObserver, this@EditNoteViewModel::deleteNoteError)
            deleteNoteDisposable?.let { composite.add(it) }
        }
    }

    private fun deleteNoteError(t: Throwable) {
        when (t) {
            is ConnectivityException -> {
                deleteNote.postValue(Resource.Error(t.message))
            }
            else -> {
                deleteNote.postValue(Resource.Error(t.message ?: "Unknown error"))
            }
        }
    }

    fun getNoteDetails(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDetails.postValue(Resource.Loading())
            noteDetailsDisposable?.let {
                composite.remove(it)
                it.dispose()
            }
            noteDetailsDisposable =
                useCase.getNoteDetails(noteId).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(noteDetailsObserver, this@EditNoteViewModel::getNoteDetailsError)
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

    fun deleteImage(image: Image) {
        if (URLUtil.isNetworkUrl(image.url)) {
            val deletingImages = deleteImages.value!!.toMutableList()
            deletingImages.add(image)
            deleteImages.postValue(deletingImages)
        }

        val value = noteDetails.value
        if (value is Resource.Success) {
            val oldNote = value.data
            val images = oldNote.images.toMutableList()
            images.remove(image)
            val newNote = Note(oldNote, images)
            noteDetails.value = Resource.Success(newNote)
        }
    }
}