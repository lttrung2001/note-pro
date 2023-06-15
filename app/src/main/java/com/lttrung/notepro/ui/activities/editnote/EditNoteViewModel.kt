package com.lttrung.notepro.ui.activities.editnote

import android.webkit.URLUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.networks.models.Image
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.repositories.NoteRepositories
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val noteRepositories: NoteRepositories
) : ViewModel() {
    private val deleteImages by lazy {
        mutableListOf<Image>()
    }

    internal val editNoteLiveData by lazy {
        MutableLiveData<Resource<Note>>()
    }

    internal val deleteNoteLiveData by lazy {
        MutableLiveData<Resource<Unit>>()
    }

    internal val noteDetailsLiveData by lazy {
        MutableLiveData<Resource<Note>>()
    }

    internal fun editNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                editNoteLiveData.postValue(Resource.Loading())
                val editMember =
                    noteRepositories.editNote(note, deleteImages.map { it.id })
                editNoteLiveData.postValue(Resource.Success(editMember))
            } catch (ex: Exception) {
                editNoteLiveData.postValue(Resource.Error(ex))
            }
        }
    }

    internal fun deleteNote(note: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                deleteNoteLiveData.postValue(Resource.Loading())
                val deleteNote =
                    noteRepositories.deleteNote(note.id)
                deleteNoteLiveData.postValue(Resource.Success(deleteNote))
            } catch (ex: Exception) {
                deleteNoteLiveData.postValue(Resource.Error(ex))
            }
        }
    }

    internal fun getNoteDetails(noteId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                noteDetailsLiveData.postValue(Resource.Loading())
                val noteDetails =
                    noteRepositories.getNoteDetails(noteId)
                noteDetailsLiveData.postValue(Resource.Success(noteDetails))
            } catch (ex: Exception) {
                noteDetailsLiveData.postValue(Resource.Error(ex))
            }
        }
    }

    fun deleteImage(image: Image) {
        if (URLUtil.isNetworkUrl(image.url)) {
            deleteImages.add(image)
        }

        val value = noteDetailsLiveData.value
        if (value is Resource.Success) {
            val note = value.data
            val images = note.images.toMutableList()
            images.remove(image)
            note.images = images
            noteDetailsLiveData.postValue(Resource.Success(note))
        }
    }
}