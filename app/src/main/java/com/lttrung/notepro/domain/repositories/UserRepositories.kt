package com.lttrung.notepro.domain.repositories

import com.lttrung.notepro.domain.data.locals.UserLocals
import com.lttrung.notepro.domain.data.locals.database.entities.CurrentUser
import com.lttrung.notepro.domain.data.networks.UserNetworks
import com.lttrung.notepro.domain.data.networks.models.UserInfo
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface UserRepositories {
    val networks: UserNetworks
    val locals: UserLocals
    fun changePassword(oldPassword: String, newPassword: String): Single<String>
    fun changeProfile(fullName: String, phoneNumber: String): Single<UserInfo>
    fun getProfile(): Single<UserInfo>
    fun getCurrentUser(): Single<CurrentUser>
    fun logout()
}