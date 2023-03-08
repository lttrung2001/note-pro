package com.lttrung.notepro.database.repositories

import com.lttrung.notepro.database.data.locals.UserLocals
import com.lttrung.notepro.database.data.networks.UserNetworks
import com.lttrung.notepro.database.data.networks.models.User
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface UserRepositories {
    val networks: UserNetworks
    val locals: UserLocals
    fun changePassword(oldPassword: String, newPassword: String): Single<Unit>
    fun changeProfile(fullName: String, phoneNumber: String): Single<User>
    fun getProfile(): Single<User>
}