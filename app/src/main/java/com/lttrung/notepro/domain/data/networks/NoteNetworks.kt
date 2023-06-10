package com.lttrung.notepro.domain.data.networks

import com.lttrung.notepro.domain.data.networks.models.Note
import javax.inject.Singleton

@Singleton
interface NoteNetworks {
    suspend fun addNote(note: Note): ResponseEntity<Note>
    suspend fun editNote(note: Note, deleteImageIds: List<String>): ResponseEntity<Note>
    suspend fun deleteNote(noteId: String): ResponseEntity<Unit>
    suspend fun getNoteDetails(noteId: String): ResponseEntity<Note>
    suspend fun getNotes(): ResponseEntity<List<Note>>
}