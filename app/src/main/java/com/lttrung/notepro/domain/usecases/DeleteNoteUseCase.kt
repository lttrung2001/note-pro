package com.lttrung.notepro.domain.usecases

import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface DeleteNoteUseCase {
    fun execute(noteId: String): Single<Unit>
}