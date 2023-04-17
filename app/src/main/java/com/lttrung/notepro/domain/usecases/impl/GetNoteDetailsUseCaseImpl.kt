package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.repositories.NoteRepositories
import com.lttrung.notepro.domain.usecases.GetNoteDetailsUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetNoteDetailsUseCaseImpl @Inject constructor(
    private val noteRepositories: NoteRepositories
) : GetNoteDetailsUseCase {
    override fun execute(noteId: String): Single<Note> {
        return noteRepositories.getNoteDetails(noteId)
    }
}