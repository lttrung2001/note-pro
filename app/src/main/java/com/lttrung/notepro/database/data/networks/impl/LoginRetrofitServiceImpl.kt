package com.lttrung.notepro.database.data.networks.impl

import com.lttrung.notepro.database.data.networks.LoginNetworks
import com.lttrung.notepro.database.data.networks.models.ApiResponse
import com.lttrung.notepro.utils.HttpStatusCodes
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.*
import javax.inject.Inject

class LoginRetrofitServiceImpl @Inject constructor(private val service: Service) : LoginNetworks {
    companion object {
        private const val PATH = "/api/v1"
    }

    interface Service {
        @FormUrlEncoded
        @POST("$PATH/login")
        fun login(
            @Field("email") email: String,
            @Field("password") password: String
        ): Single<Response<ApiResponse<String>>>

        @FormUrlEncoded
        @POST("$PATH/register")
        fun register(
            @Field("email") email: String,
            @Field("password") password: String,
            @Field("fullName") fullName: String,
            @Field("phoneNumber") phoneNumber: String
        ): Single<Response<ApiResponse<Unit>>>

        @FormUrlEncoded
        @POST("$PATH/forget-password")
        fun forgotPassword(@Field("email") email: String): Single<Response<ApiResponse<Unit>>>

        @FormUrlEncoded
        @POST("$PATH/reset-password")
        fun resetPassword(
            @Field("codeVerify") code: String,
            @Field("newPassword") password: String
        ): Single<Response<ApiResponse<Unit>>>
    }

    override fun login(email: String, password: String): Single<String> {
        return service.login(email, password).map {
            if (it.code() == HttpStatusCodes.OK.code) {
                it.body()!!.data
            } else {
                throw Exception(it.body()!!.message)
            }
        }
    }

    override fun register(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String
    ): Single<Unit> {
        return service.register(email, password, fullName, phoneNumber).map {
            if (it.code() == HttpStatusCodes.OK.code) {
                it.body()!!.data
            } else {
                throw Exception(it.body()!!.message)
            }
        }
    }

    override fun forgotPassword(email: String): Single<Unit> {
        return service.forgotPassword(email).map {
            if (it.code() == HttpStatusCodes.OK.code) {
                it.body()!!.data
            } else {
                throw Exception(it.body()!!.message)
            }
        }
    }

    override fun resetPassword(code: String, newPassword: String): Single<Unit> {
        return service.resetPassword(code, newPassword).map {
            if (it.code() == HttpStatusCodes.OK.code) {
                it.body()!!.data
            } else {
                throw Exception(it.body()!!.message)
            }
        }
    }
}