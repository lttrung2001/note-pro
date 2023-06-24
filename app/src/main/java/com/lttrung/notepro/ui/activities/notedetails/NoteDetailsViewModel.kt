package com.lttrung.notepro.ui.activities.notedetails

import androidx.lifecycle.MutableLiveData
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.repositories.NoteRepositories
import com.lttrung.notepro.ui.base.BaseViewModel
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NoteDetailsViewModel @Inject constructor(
    private val noteRepositories: NoteRepositories
) : BaseViewModel() {

    internal val noteDetailsLiveData by lazy {
        MutableLiveData<Note>()
    }

    internal fun getNoteDetails(note: Note) {
        launch {
            val noteDetails = noteRepositories.getNoteDetails(note.id)
            noteDetailsLiveData.postValue(noteDetails)
        }
    }


    internal fun editNote(note: Note) {
        launch {
            val noteDetails = noteRepositories.editNote(note, emptyList())
            noteDetailsLiveData.postValue(noteDetails)
        }
    }
}