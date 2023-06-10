package com.lttrung.notepro.domain.repositories

import com.lttrung.notepro.domain.data.locals.UserLocals
import com.lttrung.notepro.domain.data.networks.LoginNetworks
import javax.inject.Singleton

@Singleton
interface LoginRepositories {
    val locals: UserLocals
    val networks: LoginNetworks
    suspend fun login(email: String, password: String): String
    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String
    )

    suspend fun forgotPassword(email: String)
    suspend fun resetPassword(code: String, newPassword: String)
}