package com.lttrung.notepro.domain.data.networks.interceptors

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.lttrung.notepro.domain.data.locals.UserLocals
import com.lttrung.notepro.domain.data.networks.ResponseEntity
import com.lttrung.notepro.exceptions.InvalidTokenException
import com.lttrung.notepro.ui.chat.ChatSocketService
import com.lttrung.notepro.ui.login.LoginActivity
import com.lttrung.notepro.utils.AppConstant.Companion.ACCESS_TOKEN
import com.lttrung.notepro.utils.AppConstant.Companion.REFRESH_TOKEN
import com.lttrung.notepro.utils.HttpStatusCodes
import com.lttrung.notepro.utils.RetrofitUtils.BASE_URL
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.net.SocketTimeoutException
import javax.inject.Inject

class AuthorizationInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences,
    private val httpLoggingInterceptor: HttpLoggingInterceptor,
    private val userLocals: UserLocals
) : Interceptor {
    private val scope by lazy {
        CoroutineScope(Dispatchers.IO)
    }

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val builder = chain.request().newBuilder()
        // Get access token
        val accessToken = try {
            val token = sharedPreferences.getString(ACCESS_TOKEN, "")
            // If access token is empty or null, then fetch access token using refresh token
            if (token.isNullOrEmpty()) {
                fetchAccessToken()
            } else {
                // Decode access token
                val decoded = JWT(token)
                // Get exp time
                val exp = decoded.getClaim("exp").asLong()!!
                // If token expired
                if (exp < System.currentTimeMillis() / 1000 + 30) { // 30 seconds to handle request
                    fetchAccessToken()
                } else {
                    token
                }
            }
        } catch (ex: Exception) {
            requireLogin()
        }
        val newRequest = builder.addHeader("Authorization", "Bearer $accessToken").build()
        val response = chain.proceed(newRequest)
        if (response.code == HttpStatusCodes.UNAUTHORIZED.code) {
            requireLogin()
        }
        return response
    }

    private fun fetchAccessToken(): String {
        // Get refresh token
        val refreshToken = sharedPreferences.getString(REFRESH_TOKEN, "")
        if (refreshToken.isNullOrEmpty()) {
            throw InvalidTokenException("Refresh token not found or empty")
        }
        try {
            // Call api to get access token
            return callGetAccessTokenApi(refreshToken)
        } catch (ex: SocketTimeoutException) {
            throw SocketTimeoutException()
        }
    }

    private fun callGetAccessTokenApi(refreshToken: String): String {
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
        val accessToken =
            Gson().fromJson(response.body!!.string(), ResponseEntity::class.java).data as String
        scope.launch {
            userLocals.fetchAccessToken(accessToken)
        }
        return accessToken
    }

    private fun requireLogin() {
        scope.launch {
            userLocals.logout()
            withContext(Dispatchers.Main) {
                context.startActivity(Intent(context, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                context.stopService(Intent(context, ChatSocketService::class.java))
            }
        }
    }
}