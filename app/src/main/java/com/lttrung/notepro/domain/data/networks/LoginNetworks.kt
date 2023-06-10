package com.lttrung.notepro.domain.data.networks

import javax.inject.Singleton

@Singleton
interface LoginNetworks {
    suspend fun login(email: String, password: String): ResponseEntity<String>
    suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String
    ): ResponseEntity<Unit>

    suspend fun forgotPassword(email: String): ResponseEntity<Unit>
    suspend fun resetPassword(code: String, newPassword: String): ResponseEntity<Unit>
}