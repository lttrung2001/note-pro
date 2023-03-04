package com.lttrung.notepro.ui.editnote

import com.lttrung.notepro.database.data.networks.models.Note
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface EditNoteUseCase {
    fun editNote(note: Note, deleteImageIds: List<String>): Single<Note>
    fun deleteNote(noteId: String): Single<Unit>
}