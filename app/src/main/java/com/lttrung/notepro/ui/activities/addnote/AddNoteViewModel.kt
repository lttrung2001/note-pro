package com.lttrung.notepro.ui.activities.addnote

import androidx.lifecycle.MutableLiveData
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.repositories.NoteRepositories
import com.lttrung.notepro.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    private val noteRepositories: NoteRepositories
) : BaseViewModel() {
    internal val addNoteLiveData by lazy {
        MutableLiveData<Note>()
    }

    internal fun addNote(note: Note) {
        launch {
            val addNote = noteRepositories.addNote(note)
            addNoteLiveData.postValue(addNote)
        }
    }
}