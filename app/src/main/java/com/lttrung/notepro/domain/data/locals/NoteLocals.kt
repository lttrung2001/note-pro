package com.lttrung.notepro.domain.data.locals

import com.lttrung.notepro.domain.data.locals.entities.NoteLocalsModel
import javax.inject.Singleton

@Singleton
interface NoteLocals {
    suspend fun addNote(note: NoteLocalsModel)
    suspend fun addNotes(notes: List<NoteLocalsModel>)
    suspend fun editNote(note: NoteLocalsModel)
    suspend fun deleteNote(noteId: String)
    suspend fun getNoteDetails(noteId: String): NoteLocalsModel
    suspend fun getNotes(): List<NoteLocalsModel>
}