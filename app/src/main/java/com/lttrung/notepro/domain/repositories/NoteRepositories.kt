package com.lttrung.notepro.domain.repositories

import com.lttrung.notepro.domain.data.networks.NoteNetworks
import com.lttrung.notepro.domain.data.networks.models.Note
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