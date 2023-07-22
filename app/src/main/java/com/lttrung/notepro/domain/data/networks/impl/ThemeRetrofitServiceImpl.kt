package com.lttrung.notepro.domain.data.networks.impl

import com.lttrung.notepro.domain.data.networks.ResponseEntity
import com.lttrung.notepro.domain.data.networks.ThemeNetworks
import com.lttrung.notepro.domain.data.networks.models.Theme
import retrofit2.Response
import retrofit2.http.GET
import javax.inject.Inject

class ThemeRetrofitServiceImpl @Inject constructor(
    private val service: Service
) : ThemeNetworks {
    companion object {
        const val PATH = "/api/v1"
    }
    interface Service {
        @GET("$PATH/themes/")
        suspend fun fetchThemeList(): Response<ResponseEntity<List<Theme>>>
    }
    override suspend fun fetchThemeList(): ResponseEntity<List<Theme>> {
        val response = service.fetchThemeList()
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception(body?.message)
        }
    }
}