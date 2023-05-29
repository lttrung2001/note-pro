package com.lttrung.notepro.domain.data.locals

import com.lttrung.notepro.domain.data.locals.database.entities.CurrentUser
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface UserLocals {
    fun login(currentUser: CurrentUser, refreshToken: String)
    fun changePassword(password: String, refreshToken: String)
    fun changeProfile(fullName: String, phoneNumber: String)
    fun fetchAccessToken(accessToken: String)
    fun getCurrentUser(): Single<CurrentUser>
    fun getRefreshToken(): String
    fun getAccessToken(): String
    fun logout()
}