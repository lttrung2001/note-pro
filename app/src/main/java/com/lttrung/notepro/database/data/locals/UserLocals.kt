package com.lttrung.notepro.database.data.locals

import com.lttrung.notepro.database.data.locals.entities.CurrentUser
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface UserLocals {
    fun login(currentUser: CurrentUser): Single<Unit>
    fun changePassword(currentUser: CurrentUser): Single<Unit>
    fun logout(currentUser: CurrentUser): Single<Unit>
}