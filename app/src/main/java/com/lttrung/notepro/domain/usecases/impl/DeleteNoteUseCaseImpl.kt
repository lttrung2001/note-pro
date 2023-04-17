package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.repositories.NoteRepositories
import com.lttrung.notepro.domain.usecases.DeleteNoteUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class DeleteNoteUseCaseImpl @Inject constructor(
    private val noteRepositories: NoteRepositories
) : DeleteNoteUseCase {
    override fun execute(noteId: String): Single<Unit> {
        return noteRepositories.deleteNote(noteId)
    }
}