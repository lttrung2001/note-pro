package com.lttrung.notepro.database.data.networks.interceptors

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.google.gson.Gson
import com.lttrung.notepro.database.data.networks.models.ApiResponse
import com.lttrung.notepro.exceptions.InvalidTokenException
import com.lttrung.notepro.ui.login.LoginActivity
import com.lttrung.notepro.utils.AppConstant.Companion.ACCESS_TOKEN
import com.lttrung.notepro.utils.AppConstant.Companion.REFRESH_TOKEN
import com.lttrung.notepro.utils.RetrofitUtils.BASE_URL
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Inject


class AuthorizationInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences,
    private val httpLoggingInterceptor: HttpLoggingInterceptor
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        // Get access token
        val accessToken = try {
            val token = sharedPreferences.getString(ACCESS_TOKEN, "")
            // If access token is empty or null, then fetch access token using refresh token
            if (token.isNullOrEmpty()) {
                fetchAccessToken().also {
                    sharedPreferences.edit().putString(ACCESS_TOKEN, it).apply()
                }
            } else {
                token
            }
        } catch (ex: Exception) {
            context.startActivity(Intent(context, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
        val newRequest = builder.addHeader("Authorization", "Bearer $accessToken").build()
        return chain.proceed(newRequest)
    }

    private fun fetchAccessToken(): String {
        // Get refresh token
        val refreshToken = sharedPreferences.getString(REFRESH_TOKEN, "")
        if (refreshToken.isNullOrEmpty()) {
            throw InvalidTokenException("Refresh token not found or empty")
        }
        try {
            // Create client object
            val client = OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build()
            // Create form data
            val formBody: RequestBody = FormBody.Builder().add("refreshToken", refreshToken).build()
            val request =
                Request.Builder().post(formBody).url(BASE_URL + "api/v1/get-access-token")
                    .build()
            // Send request to api server and wait for response
            val response = client.newCall(request).execute()
            // Convert response to object
            return Gson().fromJson(response.body?.string(), ApiResponse::class.java).data as String
        } catch (ex: Exception) {
            throw InvalidTokenException()
        }
    }
}