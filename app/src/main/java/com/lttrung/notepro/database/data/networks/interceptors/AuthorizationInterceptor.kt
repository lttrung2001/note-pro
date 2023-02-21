package com.lttrung.notepro.database.data.networks.interceptors

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.lttrung.notepro.database.data.networks.UserNetworks
import com.lttrung.notepro.exceptions.InvalidTokenException
import com.lttrung.notepro.ui.login.LoginActivity
import com.lttrung.notepro.utils.AppConstant.Companion.ACCESS_TOKEN
import com.lttrung.notepro.utils.AppConstant.Companion.REFRESH_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthorizationInterceptor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences,
    private val userNetworks: UserNetworks

) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (isLogin(request) || isRegister(request) || isForgotPassword(request) || isFetchAccessToken(
                request
            )
        ) {
            return chain.proceed(request)
        }
        val builder = chain.request().newBuilder()
        // Get access token
        val accessToken = try {
            sharedPreferences.getString(ACCESS_TOKEN, "").also {
                if (it.isNullOrEmpty()) {
                    fetchAccessToken()
                }
            }
        } catch (ex: Exception) {
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
        val newRequest = builder.addHeader("Authorization", "Bearer $accessToken").build()
        return chain.proceed(newRequest)
    }

    private fun isFetchAccessToken(request: Request): Boolean {
        val encodedPath = request.url.encodedPath
        val method = request.method
        return encodedPath.equals("/get-access-token", true) && method.equals("get", true)
    }

    private fun fetchAccessToken(): String {
        // Get refresh token
        val refreshToken = sharedPreferences.getString(REFRESH_TOKEN, "")
        if (refreshToken.isNullOrEmpty()) {
            throw InvalidTokenException("Refresh token not found or empty")
        }
        try {
            return userNetworks.fetchAccessToken(refreshToken).blockingGet()
        } catch (ex: Exception) {
            throw InvalidTokenException()
        }
    }

    private fun isForgotPassword(request: Request): Boolean {
        val encodedPath = request.url.encodedPath
        val method = request.method
        return (encodedPath.equals(
            "/forgot-password",
            true
        ) && method.equals("post", true)) || (encodedPath.equals(
            "/reset-password",
            true
        ) && method.equals("post", true))
    }

    private fun isRegister(request: Request): Boolean {
        val encodedPath = request.url.encodedPath
        val method = request.method
        return encodedPath.equals("/register", true) && method.equals("post", true)
    }

    private fun isLogin(request: Request): Boolean {
        val encodedPath = request.url.encodedPath
        val method = request.method
        return encodedPath.equals("/login", true) && method.equals("post", true)
    }
}