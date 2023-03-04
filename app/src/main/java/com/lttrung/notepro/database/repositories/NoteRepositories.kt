package com.lttrung.notepro.database.repositories

import com.lttrung.notepro.database.data.networks.NoteNetworks
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.database.data.networks.models.Paging
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface NoteRepositories {
    val networks: NoteNetworks
    fun addNote(note: Note): Single<Note>
    fun editNote(note: Note, deleteImageIds: List<String>): Single<Note>
    fun deleteNote(noteId: String): Single<Unit>
    fun getNoteDetails(noteId: String): Single<Note>
    fun getNotes(): Single<List<Note>>
}