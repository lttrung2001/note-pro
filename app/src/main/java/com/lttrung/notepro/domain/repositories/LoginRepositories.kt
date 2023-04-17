package com.lttrung.notepro.domain.repositories

import com.lttrung.notepro.domain.data.locals.UserLocals
import com.lttrung.notepro.domain.data.networks.LoginNetworks
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface LoginRepositories {
    val locals: UserLocals
    val networks: LoginNetworks
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