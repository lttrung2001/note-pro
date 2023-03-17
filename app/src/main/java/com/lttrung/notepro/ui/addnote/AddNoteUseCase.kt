package com.lttrung.notepro.ui.addnote

import com.lttrung.notepro.database.data.locals.entities.Note
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface AddNoteUseCase {
    fun addNote(note: Note): Single<Note>
}