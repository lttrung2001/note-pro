package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.repositories.NoteRepositories
import com.lttrung.notepro.domain.usecases.GetArchivedNotesUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetArchivedNotesUseCaseImpl @Inject constructor(
    private val noteRepositories: NoteRepositories
) : GetArchivedNotesUseCase {
    override fun execute(): Single<List<Note>> {
        return noteRepositories.getNotes().map { archivedNotes ->
            archivedNotes.filter {
                it.isArchived && !it.isRemoved
            }
        }
    }
}