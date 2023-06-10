package com.lttrung.notepro.ui.notedetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.repositories.NoteRepositories
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteDetailsViewModel @Inject constructor(
    private val noteRepositories: NoteRepositories
) : ViewModel() {

    internal val noteDetailsLiveData by lazy {
        MutableLiveData<Resource<Note>>()
    }

    internal fun getNoteDetails(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                noteDetailsLiveData.postValue(Resource.Loading())
                val noteDetails = noteRepositories.getNoteDetails(note.id)
                noteDetailsLiveData.postValue(Resource.Success(noteDetails))
            } catch (ex: Exception) {
                noteDetailsLiveData.postValue(Resource.Error(ex))
            }
        }
    }


    internal fun editNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                noteDetailsLiveData.postValue(Resource.Loading())
                val noteDetails = noteRepositories.editNote(note, emptyList())
                noteDetailsLiveData.postValue(Resource.Success(noteDetails))
            } catch (ex: Exception) {
                noteDetailsLiveData.postValue(Resource.Error(ex))
            }
        }
    }
}