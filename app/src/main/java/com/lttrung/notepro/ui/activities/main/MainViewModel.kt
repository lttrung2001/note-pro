package com.lttrung.notepro.ui.activities.main

import androidx.lifecycle.MutableLiveData
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.repositories.NoteRepositories
import com.lttrung.notepro.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val noteRepositories: NoteRepositories
) : BaseViewModel() {
    init {
        getNotes()
    }

    internal val notesLiveData by lazy {
        MutableLiveData<List<Note>>()
    }
    internal val listNote by lazy {
        mutableListOf<Note>()
    }

    internal fun getNotes() {
        launch {
            val notes = noteRepositories.getNotes()
            listNote.addAll(notes)
            notesLiveData.postValue(notes)
        }
    }
}