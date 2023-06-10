package com.lttrung.notepro.domain.repositories.impl

import com.lttrung.notepro.domain.data.locals.UserLocals
import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import com.lttrung.notepro.domain.data.networks.LoginNetworks
import com.lttrung.notepro.domain.repositories.LoginRepositories
import javax.inject.Inject

class LoginRepositoriesImpl @Inject constructor(
    override val locals: UserLocals,
    override val networks: LoginNetworks
) :
    LoginRepositories {
    override suspend fun login(email: String, password: String): String {
        val refreshToken = networks.login(email, password).data
        locals.login(CurrentUser(email, password), refreshToken)
        return refreshToken
    }

    override suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String
    ) {
        return networks.register(email, password, fullName, phoneNumber).data
    }

    override suspend fun forgotPassword(email: String) {
        return networks.forgotPassword(email).data
    }

    override suspend fun resetPassword(code: String, newPassword: String) {
        return networks.resetPassword(code, newPassword).data
    }
}