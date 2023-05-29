package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.repositories.NoteRepositories
import com.lttrung.notepro.domain.usecases.GetRemovedNotesUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetRemovedNotesUseCaseImpl @Inject constructor(
    private val noteRepositories: NoteRepositories
) : GetRemovedNotesUseCase {
    override fun execute(): Single<List<Note>> {
        return noteRepositories.getNotes().map { removedNotes ->
            removedNotes.filter { it.isRemoved }
        }
    }
}