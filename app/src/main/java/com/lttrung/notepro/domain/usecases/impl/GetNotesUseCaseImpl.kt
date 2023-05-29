package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.domain.repositories.NoteRepositories
import com.lttrung.notepro.domain.usecases.GetNotesUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetNotesUseCaseImpl @Inject constructor(
    private val repositories: NoteRepositories
) : GetNotesUseCase {
    override fun execute(): Single<List<Note>> {
        return repositories.getNotes().map { allNotes ->
            allNotes.filter { !it.isArchived && !it.isRemoved }
        }
    }
}