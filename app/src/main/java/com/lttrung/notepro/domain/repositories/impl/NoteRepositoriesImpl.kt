package com.lttrung.notepro.domain.repositories.impl

import com.lttrung.notepro.domain.data.locals.NoteLocals
import com.lttrung.notepro.domain.data.networks.NoteNetworks
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.repositories.NoteRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class NoteRepositoriesImpl @Inject constructor(
    override val networks: NoteNetworks,
    override val locals: NoteLocals
) :
    NoteRepositories {
    override fun addNote(note: Note): Single<Note> {
        return networks.addNote(note).map {
            locals.addNote(note.toNoteLocalsModel())
            it
        }
    }

    override fun editNote(note: Note, deleteImageIds: List<String>): Single<Note> {
        return networks.editNote(note, deleteImageIds).map {
            locals.editNote(note.toNoteLocalsModel())
            it
        }
    }

    override fun deleteNote(noteId: String): Single<Unit> {
        return networks.deleteNote(noteId).map {
            locals.deleteNote(noteId)
        }
    }

    override fun getNoteDetails(noteId: String): Single<Note> {
        return networks.getNoteDetails(noteId).map {
            locals.addNote(it.toNoteLocalsModel())
            it
        }
    }

    override fun getNotes(): Single<List<Note>> {
        return networks.getNotes().map { ls ->
            val localsNotes = ls.map {
                it.toNoteLocalsModel()
            }
            locals.addNotes(localsNotes)
            ls
        }.onErrorReturn {
            locals.getNotes().blockingGet().map {
                it.toNoteNetworksModel()
            }
        }
    }
}