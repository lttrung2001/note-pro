package com.lttrung.notepro.domain.data.networks.impl

import com.lttrung.notepro.domain.data.networks.LoginNetworks
import com.lttrung.notepro.domain.data.networks.ResponseEntity
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
        suspend fun login(
            @Field("email") email: String,
            @Field("password") password: String
        ): Response<ResponseEntity<String>>

        @FormUrlEncoded
        @POST("$PATH/register")
        suspend fun register(
            @Field("email") email: String,
            @Field("password") password: String,
            @Field("fullName") fullName: String,
            @Field("phoneNumber") phoneNumber: String
        ): Response<ResponseEntity<Unit>>

        @FormUrlEncoded
        @POST("$PATH/forget-password")
        suspend fun forgotPassword(@Field("email") email: String): Response<ResponseEntity<Unit>>

        @FormUrlEncoded
        @POST("$PATH/reset-password")
        suspend fun resetPassword(
            @Field("codeVerify") code: String,
            @Field("newPassword") password: String
        ): Response<ResponseEntity<Unit>>
    }

    override suspend fun login(email: String, password: String): ResponseEntity<String> {
        val response = service.login(email, password)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception("Đã có lỗi xảy ra. Vui lòng thử lại sau.")
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String
    ): ResponseEntity<Unit> {
        val response = service.register(email, password, fullName, phoneNumber)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception("Đã có lỗi xảy ra. Vui lòng thử lại sau.")
        }
    }

    override suspend fun forgotPassword(email: String): ResponseEntity<Unit> {
        val response = service.forgotPassword(email)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception("Đã có lỗi xảy ra. Vui lòng thử lại sau.")
        }
    }

    override suspend fun resetPassword(code: String, newPassword: String): ResponseEntity<Unit> {
        val response = service.resetPassword(code, newPassword)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception("Đã có lỗi xảy ra. Vui lòng thử lại sau.")
        }
    }
}