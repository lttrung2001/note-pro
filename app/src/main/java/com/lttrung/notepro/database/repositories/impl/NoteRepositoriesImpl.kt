package com.lttrung.notepro.database.repositories.impl

import com.lttrung.notepro.database.data.networks.NoteNetworks
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.database.repositories.NoteRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class NoteRepositoriesImpl @Inject constructor(override val networks: NoteNetworks) :
    NoteRepositories {
    override fun addNote(note: Note): Single<Note> {
        TODO("Not yet implemented")
    }

    override fun editNote(note: Note, deleteImageIds: List<String>): Single<Note> {
        return networks.editNote(note, deleteImageIds)
    }

    override fun deleteNote(noteId: String): Single<Unit> {
        TODO("Not yet implemented")
    }

    override fun getNoteDetails(noteId: String): Single<Note> {
        return networks.getNoteDetails(noteId)
    }

    override fun getNotes(): Single<List<Note>> {
        return networks.getNotes()
    }
}