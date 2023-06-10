package com.lttrung.notepro.domain.repositories

import com.lttrung.notepro.domain.data.locals.NoteLocals
import com.lttrung.notepro.domain.data.networks.NoteNetworks
import com.lttrung.notepro.domain.data.networks.models.Note
import javax.inject.Singleton

@Singleton
interface NoteRepositories {
    val locals: NoteLocals
    val networks: NoteNetworks
    suspend fun addNote(note: Note): Note
    suspend fun editNote(note: Note, deleteImageIds: List<String>): Note
    suspend fun deleteNote(noteId: String)
    suspend fun getNoteDetails(noteId: String): Note
    suspend fun getNotes(): List<Note>
}