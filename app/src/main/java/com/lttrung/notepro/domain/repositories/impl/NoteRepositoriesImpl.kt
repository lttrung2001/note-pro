package com.lttrung.notepro.domain.repositories.impl

import com.lttrung.notepro.domain.data.locals.NoteLocals
import com.lttrung.notepro.domain.data.networks.NoteNetworks
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.repositories.NoteRepositories
import javax.inject.Inject

class NoteRepositoriesImpl @Inject constructor(
    override val networks: NoteNetworks,
    override val locals: NoteLocals
) :
    NoteRepositories {
    override suspend fun addNote(note: Note): Note {
        val responseNote = networks.addNote(note).data
        locals.addNote(responseNote.toNoteLocalsModel())
        return responseNote
    }

    override suspend fun editNote(note: Note, deleteImageIds: List<String>): Note {
        val responseNote = networks.editNote(note, deleteImageIds).data
        locals.editNote(responseNote.toNoteLocalsModel())
        return responseNote
    }

    override suspend fun deleteNote(noteId: String) {
        val response = networks.deleteNote(noteId)
        locals.deleteNote(noteId)
    }

    override suspend fun getNoteDetails(noteId: String): Note {
        return try {
            val responseNote = networks.getNoteDetails(noteId).data
            locals.addNote(responseNote.toNoteLocalsModel())
            responseNote
        } catch (ex: Exception) {
            locals.getNoteDetails(noteId).toNoteNetworksModel()
        }
    }

    override suspend fun getNotes(): List<Note> {
        return try {
            val notes = networks.getNotes().data
            locals.addNotes(notes.map { it.toNoteLocalsModel() })
            notes
        } catch (ex: Exception) {
            locals.getNotes().map { it.toNoteNetworksModel() }
        }
    }
}