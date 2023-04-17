package com.lttrung.notepro.domain.usecases

import com.lttrung.notepro.domain.data.networks.models.Note
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface GetNoteDetailsUseCase {
    fun execute(noteId: String): Single<Note>
}