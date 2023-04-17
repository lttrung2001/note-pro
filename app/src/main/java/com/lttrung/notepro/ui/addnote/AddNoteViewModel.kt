package com.lttrung.notepro.ui.addnote

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.usecases.AddNoteUseCase
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
class AddNoteViewModel @Inject constructor(
    private val addNoteUseCase: AddNoteUseCase
) : ViewModel() {
    internal val addNote: MutableLiveData<Resource<Note>> by lazy {
        MutableLiveData<Resource<Note>>()
    }

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var addNoteDisposable: Disposable? = null

    private val addNoteObserver: Consumer<Note> by lazy {
        Consumer {
            addNote.postValue(Resource.Success(it))
        }
    }

    internal fun addNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            addNote.postValue(Resource.Loading())
            addNoteDisposable?.let {
                composite.remove(it)
            }
            addNoteDisposable =
                addNoteUseCase.execute(note).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(addNoteObserver) {
                        addNote.postValue(Resource.Error(it))
                    }
            addNoteDisposable?.let { composite.add(it) }
        }
    }
}