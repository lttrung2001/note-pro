package com.lttrung.notepro.ui.main

import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.database.repositories.NoteRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class MainUseCaseImpl @Inject constructor(
    private val repositories: NoteRepositories
) : MainUseCase {
    override fun getNotes(): Single<List<Note>> {
        return repositories.getNotes()
    }
}