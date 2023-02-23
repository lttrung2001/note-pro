package com.lttrung.notepro.database.repositories.impl

import com.lttrung.notepro.database.data.locals.UserLocals
import com.lttrung.notepro.database.data.locals.entities.CurrentUser
import com.lttrung.notepro.database.data.networks.UserNetworks
import com.lttrung.notepro.database.data.networks.models.User
import com.lttrung.notepro.database.repositories.UserRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class UserRepositoriesImpl @Inject constructor(
    override val locals: UserLocals,
    override val networks: UserNetworks
) :
    UserRepositories {
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

    override fun changePassword(oldPassword: String, newPassword: String): Single<String> {
        TODO("Not yet implemented")
    }

    override fun changeProfile(fullName: String, phoneNumber: String): Single<User> {
        TODO("Not yet implemented")
    }

    override fun getProfile(): Single<User> {
        TODO("Not yet implemented")
    }

    override fun forgotPassword(email: String): Single<Unit> {
        TODO("Not yet implemented")
    }

    override fun resetPassword(code: String, newPassword: String): Single<Unit> {
        TODO("Not yet implemented")
    }

    override fun fetchAccessToken(refreshToken: String): Single<String> {
        TODO("Not yet implemented")
    }
}