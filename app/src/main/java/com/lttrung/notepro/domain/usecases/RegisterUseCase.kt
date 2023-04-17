package com.lttrung.notepro.domain.usecases

import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface RegisterUseCase {
    fun execute(email: String, password: String, fullName: String, phoneNumber: String): Single<Unit>
}