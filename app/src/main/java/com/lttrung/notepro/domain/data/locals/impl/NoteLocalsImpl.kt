package com.lttrung.notepro.domain.data.locals.impl

import com.lttrung.notepro.domain.data.locals.NoteLocals
import com.lttrung.notepro.domain.data.locals.database.entities.NoteLocalsModel
import com.lttrung.notepro.domain.data.locals.database.dao.NoteDao
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class NoteLocalsImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteLocals {
    override fun addNote(note: NoteLocalsModel) {
        return noteDao.addNote(note)
    }

    override fun addNotes(notes: List<NoteLocalsModel>) {
        return noteDao.addNotes(notes)
    }

    override fun editNote(note: NoteLocalsModel) {
        return noteDao.editNote(note)
    }

    override fun deleteNote(noteId: String) {
        val deletingNote = getNoteDetails(noteId).blockingGet()
        return noteDao.deleteNote(deletingNote)
    }

    override fun getNoteDetails(noteId: String): Single<NoteLocalsModel> {
        return noteDao.getNoteDetails(noteId)
    }

    override fun getNotes(): Single<List<NoteLocalsModel>> {
        return noteDao.getNotes()
    }
}