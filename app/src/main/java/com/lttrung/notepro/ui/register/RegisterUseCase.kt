package com.lttrung.notepro.ui.register

import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface RegisterUseCase {
    fun register(email: String, password: String, fullName: String, phoneNumber: String): Single<Unit>
}