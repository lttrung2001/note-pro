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
    override fun changePassword(oldPassword: String, newPassword: String): Single<Unit> {
        TODO("Not yet implemented")
    }

    override fun changeProfile(fullName: String, phoneNumber: String): Single<User> {
        TODO("Not yet implemented")
    }

    override fun getProfile(): Single<User> {
        return networks.getProfile()
    }

}