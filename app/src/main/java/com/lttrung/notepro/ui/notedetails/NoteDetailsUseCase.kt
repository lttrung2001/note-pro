package com.lttrung.notepro.ui.notedetails

import com.lttrung.notepro.database.data.locals.entities.Note
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface NoteDetailsUseCase {
    fun getNoteDetails(note: Note): Single<Note>
    fun updatePin(noteId: String, isPin: Boolean): Single<Boolean>
}