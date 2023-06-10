package com.lttrung.notepro.domain.data.locals.impl

import com.lttrung.notepro.domain.data.locals.NoteLocals
import com.lttrung.notepro.domain.data.locals.dao.NoteDao
import com.lttrung.notepro.domain.data.locals.entities.NoteLocalsModel
import javax.inject.Inject

class NoteLocalsImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteLocals {
    override suspend fun addNote(note: NoteLocalsModel) {
        noteDao.addNote(note)
    }

    override suspend fun addNotes(notes: List<NoteLocalsModel>) {
        noteDao.addNotes(notes)
    }

    override suspend fun editNote(note: NoteLocalsModel) {
        noteDao.editNote(note)
    }

    override suspend fun deleteNote(noteId: String) {
        val deletingNote = getNoteDetails(noteId)
        noteDao.deleteNote(deletingNote)
    }

    override suspend fun getNoteDetails(noteId: String): NoteLocalsModel {
        return noteDao.getNoteDetails(noteId)
    }

    override suspend fun getNotes(): List<NoteLocalsModel> {
        return noteDao.getNotes()
    }
}