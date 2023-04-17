package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.repositories.NoteRepositories
import com.lttrung.notepro.domain.usecases.EditNoteUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class EditNoteUseCaseImpl @Inject constructor(
    private val repositories: NoteRepositories
) : EditNoteUseCase {
    override fun execute(note: Note, deleteImageIds: List<String>): Single<Note> {
        return repositories.editNote(note, deleteImageIds)
    }
}