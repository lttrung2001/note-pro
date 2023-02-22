package com.lttrung.notepro.database.repositories.impl

import com.lttrung.notepro.database.data.networks.models.User
import com.lttrung.notepro.database.repositories.UserRepositories
import io.reactivex.rxjava3.core.Single

class UserRepositoriesImpl : UserRepositories {
    override fun login(email: String, password: String): Single<String> {
        TODO("Not yet implemented")
    }

    override fun register(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String
    ): Single<Unit> {
        TODO("Not yet implemented")
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