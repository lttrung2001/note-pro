package com.lttrung.notepro.domain.usecases

import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface DeleteMemberUseCase {
    fun execute(noteId: String, memberId: String): Single<Unit>
}