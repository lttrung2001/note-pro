package com.lttrung.notepro.ui.editnote

import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.database.repositories.NoteRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class EditNoteUseCaseImpl @Inject constructor(
    private val repositories: NoteRepositories
) : EditNoteUseCase {
    override fun editNote(note: Note, deleteImageIds: List<String>): Single<Note> {
        return repositories.editNote(note, deleteImageIds)
    }

    override fun deleteNote(noteId: String): Single<Unit> {
        TODO("Not yet implemented")
    }

}