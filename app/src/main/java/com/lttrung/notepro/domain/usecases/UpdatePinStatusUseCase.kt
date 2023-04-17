package com.lttrung.notepro.domain.usecases

import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface UpdatePinStatusUseCase {
    fun execute(noteId: String, isPin: Boolean): Single<Boolean>
}