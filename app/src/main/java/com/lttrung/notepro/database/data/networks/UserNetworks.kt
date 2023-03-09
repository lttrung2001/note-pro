package com.lttrung.notepro.database.data.networks

import com.lttrung.notepro.database.data.networks.models.User
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface UserNetworks {
    fun changePassword(oldPassword: String, newPassword: String): Single<Unit>
    fun changeProfile(fullName: String, phoneNumber: String): Single<User>
    fun getProfile(): Single<User>
}