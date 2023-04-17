package com.lttrung.notepro.ui.editnote

import android.webkit.URLUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.networks.models.Image
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.usecases.DeleteNoteUseCase
import com.lttrung.notepro.domain.usecases.EditNoteUseCase
import com.lttrung.notepro.domain.usecases.GetNoteDetailsUseCase
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
    private val editNoteUseCase: EditNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val getNoteDetailsUseCase: GetNoteDetailsUseCase
) : ViewModel() {
    private val deleteImages: MutableLiveData<List<Image>> by lazy {
        MutableLiveData<List<Image>>(emptyList())
    }

    internal val editNote: MutableLiveData<Resource<Note>> by lazy {
        MutableLiveData<Resource<Note>>()
    }

    internal val deleteNote: MutableLiveData<Resource<Unit>> by lazy {
        MutableLiveData<Resource<Unit>>()
    }

    internal val noteDetails: MutableLiveData<Resource<Note>> by lazy {
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

    internal fun editNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            editNote.postValue(Resource.Loading())
            editNoteDisposable?.let {
                composite.remove(it)
                it.dispose()
            }
            val deletingImageIds = deleteImages.value!!.map {
                it.id
            }
            editNoteDisposable = editNoteUseCase.execute(note, deletingImageIds)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(editNoteObserver) {
                    editNote.postValue(Resource.Error(it))
                }
            editNoteDisposable?.let { composite.add(it) }
        }
    }

    internal fun deleteNote(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteNote.postValue(Resource.Loading())
            deleteNoteDisposable?.let {
                composite.remove(it)
            }
            deleteNoteDisposable =
                deleteNoteUseCase.execute(noteId).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(deleteNoteObserver) {
                        deleteNote.postValue(Resource.Error(it))
                    }
            deleteNoteDisposable?.let { composite.add(it) }
        }
    }

    internal fun getNoteDetails(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            noteDetails.postValue(Resource.Loading())
            noteDetailsDisposable?.let {
                composite.remove(it)
                it.dispose()
            }
            noteDetailsDisposable?.let { composite.add(it) }
            noteDetailsDisposable =
                getNoteDetailsUseCase.execute(noteId).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(noteDetailsObserver) {
                        noteDetails.postValue(Resource.Error(it))
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