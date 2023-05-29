package com.lttrung.notepro.domain.data.locals

import com.lttrung.notepro.domain.data.locals.database.entities.NoteLocalsModel
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface NoteLocals {
    fun addNote(note: NoteLocalsModel)
    fun addNotes(notes: List<NoteLocalsModel>)
    fun editNote(note: NoteLocalsModel)
    fun deleteNote(noteId: String)
    fun getNoteDetails(noteId: String): Single<NoteLocalsModel>
    fun getNotes(): Single<List<NoteLocalsModel>>
}