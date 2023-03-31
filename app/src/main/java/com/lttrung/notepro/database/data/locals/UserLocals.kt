package com.lttrung.notepro.database.data.locals

import com.lttrung.notepro.database.data.locals.entities.CurrentUser
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface UserLocals {
    fun login(currentUser: CurrentUser, refreshToken: String)
    fun changePassword(password: String, refreshToken: String)
    fun changeProfile(fullName: String, phoneNumber: String)
    fun fetchAccessToken(accessToken: String)
    fun getCurrentUserInfo(): CurrentUser
    fun getCurrentUserId(): String
    fun logout()
}