package com.lttrung.notepro.database.data.networks

import com.lttrung.notepro.database.data.networks.models.Note
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface NoteNetworks {
    fun addNote(note: Note): Single<Note>
    fun editNote(note: Note): Single<Note>
    fun deleteNote(noteId: String): Single<Unit>
    fun getNoteDetails(noteId: String): Single<Note>
    fun getNotes(): Single<List<Note>>
}