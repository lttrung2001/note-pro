package com.lttrung.notepro.ui.activities.addnote

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
class AddNoteViewModel @Inject constructor(
    private val noteRepositories: NoteRepositories
) : ViewModel() {
    internal val addNoteLiveData by lazy {
        MutableLiveData<Resource<Note>>()
    }
    internal fun addNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            addNoteLiveData.postValue(Resource.Loading())
            try {
                addNoteLiveData.postValue(Resource.Loading())
                val refreshToken = noteRepositories.addNote(note)
                addNoteLiveData.postValue(Resource.Success(refreshToken))
            } catch (ex: Exception) {
                addNoteLiveData.postValue(Resource.Error(ex))
            }
        }
    }
}