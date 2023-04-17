package com.lttrung.notepro.domain.usecases

import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ResetPasswordUseCase {
    fun execute(code: String, newPassword: String): Single<Unit>
}