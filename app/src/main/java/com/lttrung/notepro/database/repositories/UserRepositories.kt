package com.lttrung.notepro.database.repositories

import com.lttrung.notepro.database.data.networks.models.User
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface UserRepositories {
    fun login(email: String, password: String): Single<String>
    fun register(email: String, password: String, fullName: String, phoneNumber: String): Single<Unit>

    fun changePassword(oldPassword: String, newPassword: String): Single<String>
    fun changeProfile(fullName: String, phoneNumber: String): Single<User>
    fun getProfile(): Single<User>

    fun forgotPassword(email: String): Single<Unit>
    fun resetPassword(code: String, newPassword: String): Single<Unit>

    fun fetchAccessToken(refreshToken: String): Single<String>
}