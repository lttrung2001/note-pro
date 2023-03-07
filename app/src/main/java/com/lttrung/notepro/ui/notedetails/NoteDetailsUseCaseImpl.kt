package com.lttrung.notepro.ui.notedetails

import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.database.repositories.NoteRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class NoteDetailsUseCaseImpl @Inject constructor(
    private val repositories: NoteRepositories
) : NoteDetailsUseCase {
    override fun getNoteDetails(note: Note): Single<Note> {
        return repositories.getNoteDetails(note.id)
    }
}