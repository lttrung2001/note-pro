package com.lttrung.notepro.database.repositories.impl

import com.lttrung.notepro.database.data.locals.UserLocals
import com.lttrung.notepro.database.data.networks.UserNetworks
import com.lttrung.notepro.database.data.networks.models.User
import com.lttrung.notepro.database.repositories.UserRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class UserRepositoriesImpl @Inject constructor(
    override val networks: UserNetworks,
    override val locals: UserLocals
) : UserRepositories {
    override fun changePassword(oldPassword: String, newPassword: String): Single<String> {
        return networks.changePassword(oldPassword, newPassword).doAfterSuccess { refreshToken ->
            locals.changePassword(newPassword, refreshToken)
        }
    }

    override fun changeProfile(fullName: String, phoneNumber: String): Single<User> {
        return networks.changeProfile(fullName, phoneNumber).doAfterSuccess {
            locals.changeProfile(fullName, phoneNumber)
        }
    }

    override fun getProfile(): Single<User> {
        return networks.getProfile().doAfterSuccess { user ->
            locals.changeProfile(user.fullName, user.phoneNumber)
        }
    }
}