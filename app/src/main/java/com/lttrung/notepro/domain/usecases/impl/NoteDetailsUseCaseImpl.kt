package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.repositories.NoteRepositories
import com.lttrung.notepro.domain.usecases.NoteDetailsUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class NoteDetailsUseCaseImpl @Inject constructor(
    private val repositories: NoteRepositories,
) : NoteDetailsUseCase {
    override fun execute(note: Note): Single<Note> {
        return repositories.getNoteDetails(note.id)
    }
}