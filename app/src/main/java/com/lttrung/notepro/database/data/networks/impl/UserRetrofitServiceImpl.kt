package com.lttrung.notepro.database.data.networks.impl

import com.lttrung.notepro.database.data.networks.UserNetworks
import com.lttrung.notepro.database.data.networks.models.*
import com.lttrung.notepro.utils.HttpStatusCodes
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.*
import javax.inject.Inject

class UserRetrofitServiceImpl @Inject constructor(private val service: Service) : UserNetworks {
    interface Service {

    }

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

    override fun changePassword(oldPassword: String, newPassword: String): Single<Unit> {
        TODO("Not yet implemented")
    }

    override fun changeProfile(fullName: String, phoneNumber: String): Single<Unit> {
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