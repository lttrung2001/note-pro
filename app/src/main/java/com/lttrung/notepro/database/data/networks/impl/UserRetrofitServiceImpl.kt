package com.lttrung.notepro.database.data.networks.impl

import com.lttrung.notepro.database.data.networks.UserNetworks
import com.lttrung.notepro.database.data.networks.models.ApiResponse
import com.lttrung.notepro.database.data.networks.models.User
import com.lttrung.notepro.utils.HttpStatusCodes
import io.reactivex.rxjava3.core.Single
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import javax.inject.Inject

class UserRetrofitServiceImpl @Inject constructor(private val service: Service) : UserNetworks {
    interface Service {
        @FormUrlEncoded
        @POST("/login")
        fun login(
            @Field("email") email: String,
            @Field("password") password: String
        ): Single<Response<ApiResponse<String>>>
    }

    override fun login(email: String, password: String): Single<String> {
        return service.login(email, password).map {
            if (it.code() == HttpStatusCodes.OK.code) {
                it.body()!!.data
            } else {
                throw Error(it.body()!!.message)
            }
        }
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