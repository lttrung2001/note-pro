package com.lttrung.notepro.database.data.locals

import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface UserLocals {
    fun login(email: String, password: String): Single<Unit>
    fun logout(email: String): Single<Unit>
}