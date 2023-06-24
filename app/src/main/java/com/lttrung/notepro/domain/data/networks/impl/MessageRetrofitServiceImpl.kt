package com.lttrung.notepro.domain.data.networks.impl

import com.lttrung.notepro.domain.data.networks.MessageNetworks
import com.lttrung.notepro.domain.data.networks.ResponseEntity
import com.lttrung.notepro.domain.data.networks.models.Message
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

class MessageRetrofitServiceImpl @Inject constructor(
    private val service: Service
) : MessageNetworks {
    companion object {
        private const val PATH = "/api/v1/messages"
    }

    interface Service {
        @GET("$PATH/get-messages")
        fun fetchMessages(
            @Query("roomId") roomId: String,
            @Query("pageIndex") pageIndex: Int,
            @Query("limit") limit: Int
        ): Response<ResponseEntity<List<Message>>>
    }

    override fun fetchMessages(
        roomId: String,
        pageIndex: Int,
        limit: Int
    ): ResponseEntity<List<Message>> {
        val response = service.fetchMessages(roomId, pageIndex, limit)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception(body?.message)
        }
    }
}