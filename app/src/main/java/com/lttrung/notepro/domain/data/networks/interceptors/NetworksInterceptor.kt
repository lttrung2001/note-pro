package com.lttrung.notepro.domain.data.networks.interceptors

import android.content.Context
import android.net.ConnectivityManager
import com.lttrung.notepro.exceptions.NoInternetException
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class NetworksInterceptor @Inject constructor(@ApplicationContext private val context: Context) :
    Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isConnected) {
            throw NoInternetException()
        } else {
            val request = chain.request()
            return chain.proceed(request)
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