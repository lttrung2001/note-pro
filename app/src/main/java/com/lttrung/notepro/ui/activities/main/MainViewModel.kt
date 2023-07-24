package com.lttrung.notepro.ui.activities.main

import androidx.lifecycle.MutableLiveData
import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.repositories.NoteRepositories
import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val noteRepositories: NoteRepositories,
    private val userRepositories: UserRepositories
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
            getCurrentUserInfo()
        }
    }

    internal val userLiveData by lazy {
        MutableLiveData<CurrentUser>()
    }

    internal fun getCurrentUserInfo() {
        launch {
            val user = userRepositories.getCurrentUser()
            userLiveData.postValue(user)
        }
    }

    internal fun logout() {
        launch {
            userRepositories.logout()
        }
    }
}