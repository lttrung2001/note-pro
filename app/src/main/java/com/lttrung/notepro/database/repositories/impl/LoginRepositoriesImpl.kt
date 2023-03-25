package com.lttrung.notepro.database.repositories.impl

import com.lttrung.notepro.database.data.locals.UserLocals
import com.lttrung.notepro.database.data.locals.entities.CurrentUser
import com.lttrung.notepro.database.data.networks.LoginNetworks
import com.lttrung.notepro.database.repositories.LoginRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class LoginRepositoriesImpl @Inject constructor(
    override val locals: UserLocals,
    override val networks: LoginNetworks
) :
    LoginRepositories {
    override fun login(email: String, password: String): Single<String> {
        return networks.login(email, password).doAfterSuccess { refreshToken ->
            locals.login(CurrentUser(email, password), refreshToken)
        }
    }

    override fun register(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String
    ): Single<Unit> {
        return networks.register(email, password, fullName, phoneNumber)
    }

    override fun forgotPassword(email: String): Single<Unit> {
        return networks.forgotPassword(email)
    }

    override fun resetPassword(code: String, newPassword: String): Single<Unit> {
        return networks.resetPassword(code, newPassword)
    }
}