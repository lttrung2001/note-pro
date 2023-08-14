package com.lttrung.notepro.domain.data.networks.interceptors

import android.content.Context
import android.net.ConnectivityManager
import com.lttrung.notepro.exceptions.NoInternetException
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import javax.inject.Inject

class NetworksInterceptor @Inject constructor(@ApplicationContext private val context: Context) :
    Interceptor {
    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (!isConnected) {
            Response.Builder()
                .protocol(Protocol.HTTP_1_1)
                .request(chain.request())
                .code(1000)
                .message("No internet connection")
                .body(ResponseBody.create("".toMediaTypeOrNull(), ""))
                .build()
        } else {
            val request = chain.request()
            chain.proceed(request)
        }
    }

    private val isConnected: Boolean
        get() {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            return netInfo != null && netInfo.isConnected
        }
}