package com.lttrung.notepro.domain.data.networks.impl

import com.lttrung.notepro.domain.data.networks.ResponseEntity
import com.lttrung.notepro.domain.data.networks.UserNetworks
import com.lttrung.notepro.domain.data.networks.models.UserInfo
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PUT
import javax.inject.Inject

class UserRetrofitServiceImpl @Inject constructor(
    private val service: Service
) : UserNetworks {
    companion object {
        private const val PATH = "/api/v1"
    }

    interface Service {
        @GET("$PATH/get-user-details")
        suspend fun getProfile(): Response<ResponseEntity<UserInfo>>

        @FormUrlEncoded
        @PUT("$PATH/change-infor")
        suspend fun changeProfile(
            @Field("fullName") fullName: String,
            @Field("phoneNumber") phoneNumber: String
        ): Response<ResponseEntity<UserInfo>>

        @FormUrlEncoded
        @PUT("$PATH/change-password")
        suspend fun changePassword(
            @Field("oldPassword") oldPassword: String,
            @Field("newPassword") newPassword: String
        ): Response<ResponseEntity<String>>
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): ResponseEntity<String> {
        val response = service.changePassword(oldPassword, newPassword)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception("Đã có lỗi xảy ra. Vui lòng thử lại sau.")
        }
    }

    override suspend fun changeProfile(
        fullName: String,
        phoneNumber: String
    ): ResponseEntity<UserInfo> {
        val response = service.changeProfile(fullName, phoneNumber)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception("Đã có lỗi xảy ra. Vui lòng thử lại sau.")
        }
    }

    override suspend fun getProfile(): ResponseEntity<UserInfo> {
        val response = service.getProfile()
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception("Đã có lỗi xảy ra. Vui lòng thử lại sau.")
        }
    }
}