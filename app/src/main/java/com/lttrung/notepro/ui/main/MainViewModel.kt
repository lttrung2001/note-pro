package com.lttrung.notepro.ui.main

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
class MainViewModel @Inject constructor(
    private val noteRepositories: NoteRepositories
) : ViewModel() {
    init {
        getNotes(GET_CURRENT_NOTES)
    }

    internal val notesLiveData by lazy {
        MutableLiveData<Resource<List<Note>>>()
    }

    internal fun getNotes(getNotesType: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                notesLiveData.postValue(Resource.Loading())
                val notes = noteRepositories.getNotes()
                notesLiveData.postValue(Resource.Success(notes))
            } catch (ex: Exception) {
                notesLiveData.postValue(Resource.Error(ex))
            }
        }
    }

    companion object {
        internal const val GET_CURRENT_NOTES = 1
        internal const val GET_ARCHIVED_NOTES = 2
        internal const val GET_REMOVED_NOTES = 3
    }
}