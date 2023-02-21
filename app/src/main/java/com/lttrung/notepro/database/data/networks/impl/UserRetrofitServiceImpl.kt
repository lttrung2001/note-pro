package com.lttrung.notepro.database.data.networks.impl

import com.lttrung.notepro.database.data.networks.UserNetworks
import com.lttrung.notepro.database.data.networks.models.*
import com.lttrung.notepro.utils.HttpStatusCodes
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import javax.inject.Inject

class UserRetrofitServiceImpl @Inject constructor(private val service: Service) : UserNetworks {
    interface Service {
        @POST("/login")
        fun login(@Body loginBody: LoginBody): Single<Response<ApiResponse<String>>>

        @POST("/register")
        fun register(@Body registerBody: RegisterBody): Single<Response<ApiResponse<Unit>>>

        @PUT("/change-password")
        fun changePassword(@Body changePasswordBody: ChangePasswordBody): Single<Response<ApiResponse<Unit>>>

        @PUT("/change-infor")
        fun changeInfo(@Body changeInfoBody: ChangeInfoBody): Single<Response<ApiResponse<Unit>>>

        @GET("/get-profile")
        fun getProfile(): Single<Response<ApiResponse<User>>>

        @POST("/forgot-password")
        // Cần thay đổi body
        fun forgotPassword(@Body email: String): Single<Response<ApiResponse<Unit>>>

        @POST("/reset-password")
        fun resetPassword(@Body resetPasswordBody: ResetPasswordBody): Single<Response<ApiResponse<Unit>>>

        @GET("/get-access-token")
        fun fetchAccessToken(@Body refreshToken: String): Single<Response<ApiResponse<String>>>
    }

    override fun login(email: String, password: String): Single<String> {
        val body = LoginBody(email, password)
        return service.login(body).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.body()!!.message)
            }
        }
    }

    override fun register(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String
    ): Single<Unit> {
        val body = RegisterBody(email, password, fullName, phoneNumber)
        return service.register(body).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.body()!!.message)
            }
        }
    }

    override fun changePassword(oldPassword: String, newPassword: String): Single<Unit> {
        val body = ChangePasswordBody(oldPassword, newPassword)
        return service.changePassword(body).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.body()!!.message)
            }
        }
    }

    override fun changeProfile(fullName: String, phoneNumber: String): Single<Unit> {
        val body = ChangeInfoBody(fullName, phoneNumber)
        return service.changeInfo(body).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.body()!!.message)
            }
        }
    }

    override fun getProfile(): Single<User> {
        return service.getProfile().map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.body()!!.message)
            }
        }
    }

    override fun forgotPassword(email: String): Single<Unit> {
        return service.forgotPassword(email).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.body()!!.message)
            }
        }
    }

    override fun resetPassword(code: String, newPassword: String): Single<Unit> {
        val body = ResetPasswordBody(code, newPassword)
        return service.resetPassword(body).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.body()!!.message)
            }
        }
    }

    override fun fetchAccessToken(refreshToken: String): Single<String> {
        return service.fetchAccessToken(refreshToken).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.body()!!.message)
            }
        }
    }
}