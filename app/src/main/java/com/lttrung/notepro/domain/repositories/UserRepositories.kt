package com.lttrung.notepro.domain.repositories

import com.lttrung.notepro.domain.data.locals.UserLocals
import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import com.lttrung.notepro.domain.data.networks.UserNetworks
import com.lttrung.notepro.domain.data.networks.models.UserInfo
import javax.inject.Singleton

@Singleton
interface UserRepositories {
    val networks: UserNetworks
    val locals: UserLocals
    suspend fun changePassword(oldPassword: String, newPassword: String): String
    suspend fun changeProfile(fullName: String, phoneNumber: String): UserInfo
    suspend fun getProfile(): UserInfo
    suspend fun getCurrentUser(): CurrentUser
    suspend fun logout()
}