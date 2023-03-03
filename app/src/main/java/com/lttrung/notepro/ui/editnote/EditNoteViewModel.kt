package com.lttrung.notepro.ui.editnote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val editNote: MutableLiveData<Resource<Note>> by lazy {
        MutableLiveData<Resource<Note>>()
    }

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var editNoteDisposable: Disposable? = null
    private val editNoteObserver: Consumer<Note> by lazy {
        Consumer {
            editNote.postValue(Resource.Success(it))
        }
    }

    fun editNote(note: Note, deleteImageIds: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            editNote.postValue(Resource.Loading())
            editNoteDisposable?.let {
                composite.remove(it)
                it.dispose()
            }
            editNoteDisposable =
                useCase.editNote(note, deleteImageIds).observeOn(AndroidSchedulers.mainThread())
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
}