package com.lttrung.notepro.domain.data.networks

import com.lttrung.notepro.domain.data.networks.models.UserInfo
import javax.inject.Singleton

@Singleton
interface UserNetworks {
    suspend fun changePassword(oldPassword: String, newPassword: String): ResponseEntity<String>
    suspend fun changeProfile(fullName: String, phoneNumber: String): ResponseEntity<UserInfo>
    suspend fun getProfile(): ResponseEntity<UserInfo>
}