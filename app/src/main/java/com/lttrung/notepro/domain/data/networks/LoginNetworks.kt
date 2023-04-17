package com.lttrung.notepro.domain.data.networks

import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface LoginNetworks {
    fun login(email: String, password: String): Single<String>
    fun register(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String
    ): Single<Unit>

    fun forgotPassword(email: String): Single<Unit>
    fun resetPassword(code: String, newPassword: String): Single<Unit>
}