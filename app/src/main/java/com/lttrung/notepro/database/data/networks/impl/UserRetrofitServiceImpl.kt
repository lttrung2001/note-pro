package com.lttrung.notepro.database.data.networks.impl

import com.lttrung.notepro.database.data.networks.UserNetworks
import com.lttrung.notepro.database.data.networks.models.ApiResponse
import com.lttrung.notepro.database.data.networks.models.User
import com.lttrung.notepro.utils.HttpStatusCodes
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.GET
import javax.inject.Inject

class UserRetrofitServiceImpl @Inject constructor(
    private val service: Service
) : UserNetworks {
    interface Service {
        @GET("$PATH/get-user-details")
        fun getProfile(): Single<Response<ApiResponse<User>>>
    }

    override fun changePassword(oldPassword: String, newPassword: String): Single<Unit> {
        TODO("Not yet implemented")
    }

    override fun changeProfile(fullName: String, phoneNumber: String): Single<Unit> {
        TODO("Not yet implemented")
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

    companion object {
        private const val PATH = "/api/v1"
    }
}