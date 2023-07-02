package com.lttrung.notepro.ui.activities.editnote

import android.webkit.URLUtil
import androidx.lifecycle.MutableLiveData
import com.lttrung.notepro.domain.data.networks.models.Image
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.repositories.NoteRepositories
import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    private val noteRepositories: NoteRepositories, private val userRepositories: UserRepositories
) : BaseViewModel() {
    private val deleteImages by lazy {
        mutableListOf<Image>()
    }

    internal val editNoteLiveData by lazy {
        MutableLiveData<Note>()
    }

    internal val deleteNoteLiveData by lazy {
        MutableLiveData<Unit>()
    }

    internal val noteDetailsLiveData by lazy {
        MutableLiveData<Note>()
    }

    internal fun editNote(note: Note) {
        launch {
            val editMember = noteRepositories.editNote(note, deleteImages.map { it.id })
            editNoteLiveData.postValue(editMember)
        }
    }

    internal fun deleteNote(note: Note) {
        launch {
            val deleteNote = noteRepositories.deleteNote(note.id)
            deleteNoteLiveData.postValue(deleteNote)
        }
    }

    internal fun getNoteDetails(noteId: String) {
        launch {
            val noteDetails = noteRepositories.getNoteDetails(noteId)
            noteDetailsLiveData.postValue(noteDetails)
        }
    }

    fun deleteImage(note: Note, image: Image) {
        if (URLUtil.isNetworkUrl(image.url)) {
            deleteImages.add(image)
        }

        val images = note.images.toMutableList()
        images.remove(image)
        note.images = images
        noteDetailsLiveData.postValue(note)
    }
}