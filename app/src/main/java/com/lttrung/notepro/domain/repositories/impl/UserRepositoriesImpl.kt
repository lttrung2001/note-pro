package com.lttrung.notepro.domain.repositories.impl

import com.lttrung.notepro.domain.data.locals.UserLocals
import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import com.lttrung.notepro.domain.data.networks.UserNetworks
import com.lttrung.notepro.domain.data.networks.models.UserInfo
import com.lttrung.notepro.domain.repositories.UserRepositories
import javax.inject.Inject

class UserRepositoriesImpl @Inject constructor(
    override val networks: UserNetworks,
    override val locals: UserLocals
) : UserRepositories {
    override suspend fun changePassword(oldPassword: String, newPassword: String): String {
        val refreshToken = networks.changePassword(oldPassword, newPassword).data
        locals.changePassword(newPassword, refreshToken)
        return refreshToken
    }

    override suspend fun changeProfile(fullName: String, phoneNumber: String): UserInfo {
        val userInfo = networks.changeProfile(fullName, phoneNumber).data
        locals.changeProfile(fullName, phoneNumber)
        return userInfo
    }

    override suspend fun getProfile(): UserInfo {
        return try {
            val userInfo = networks.getProfile().data
            locals.changeProfile(userInfo.fullName, userInfo.phoneNumber)
            userInfo
        } catch (ex: Exception) {
            val user = locals.getCurrentUser()
            if (user.id != null && user.fullName != null && user.phoneNumber != null) {
                UserInfo(user.id!!, user.email, user.fullName!!, user.phoneNumber!!)
            } else {
                throw ex
            }
        }
    }

    override suspend fun getCurrentUser(): CurrentUser {
        return locals.getCurrentUser()
    }

    override suspend fun logout() {
        return locals.logout()
    }
}