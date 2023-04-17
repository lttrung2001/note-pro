package com.lttrung.notepro.domain.repositories.impl

import com.lttrung.notepro.domain.data.networks.NoteNetworks
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.repositories.NoteRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class NoteRepositoriesImpl @Inject constructor(override val networks: NoteNetworks) :
    NoteRepositories {
    override fun addNote(note: Note): Single<Note> {
        return networks.addNote(note)
    }

    override fun editNote(note: Note, deleteImageIds: List<String>): Single<Note> {
        return networks.editNote(note, deleteImageIds)
    }

    override fun deleteNote(noteId: String): Single<Unit> {
        return networks.deleteNote(noteId)
    }

    override fun getNoteDetails(noteId: String): Single<Note> {
        return networks.getNoteDetails(noteId)
    }

    override fun getNotes(): Single<List<Note>> {
        return networks.getNotes()
    }
}