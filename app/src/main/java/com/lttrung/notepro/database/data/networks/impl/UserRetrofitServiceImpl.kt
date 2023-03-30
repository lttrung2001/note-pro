package com.lttrung.notepro.database.data.networks.impl

import com.lttrung.notepro.database.data.networks.UserNetworks
import com.lttrung.notepro.database.data.networks.models.ApiResponse
import com.lttrung.notepro.database.data.networks.models.UserInfo
import com.lttrung.notepro.utils.HttpStatusCodes
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.PUT
import javax.inject.Inject

class UserRetrofitServiceImpl @Inject constructor(
    private val service: Service
) : UserNetworks {
    interface Service {
        @GET("$PATH/get-user-details")
        fun getProfile(): Single<Response<ApiResponse<UserInfo>>>

        @FormUrlEncoded
        @PUT("$PATH/change-infor")
        fun changeProfile(
            @Field("fullName") fullName: String,
            @Field("phoneNumber") phoneNumber: String
        ): Single<Response<ApiResponse<UserInfo>>>

        @FormUrlEncoded
        @PUT("$PATH/change-password")
        fun changePassword(
            @Field("oldPassword") oldPassword: String,
            @Field("newPassword") newPassword: String
        ): Single<Response<ApiResponse<String>>>
    }

    override fun changePassword(oldPassword: String, newPassword: String): Single<String> {
        return service.changePassword(oldPassword, newPassword).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.body()!!.message)
            }
        }
    }

    override fun changeProfile(fullName: String, phoneNumber: String): Single<UserInfo> {
        return service.changeProfile(fullName, phoneNumber).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.body()!!.message)
            }
        }
    }

    override fun getProfile(): Single<UserInfo> {
        return service.getProfile().map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.body()!!.message)
            }
        }
    }

    companion object {
        private const val PATH = "/api/v1"
    }
}