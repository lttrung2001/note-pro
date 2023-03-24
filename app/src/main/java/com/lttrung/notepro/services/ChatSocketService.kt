package com.lttrung.notepro.services

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.lttrung.notepro.database.data.locals.entities.Message
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.ACCESS_TOKEN
import com.lttrung.notepro.utils.NotificationHelper
import com.lttrung.notepro.utils.RetrofitUtils.BASE_URL
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import io.socket.client.IO
import io.socket.client.Socket
import javax.inject.Inject

@AndroidEntryPoint
class ChatSocketService : Service() {
    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var gson: Gson

    val messageLiveData = MutableLiveData<Message>()

    private val binder: Binder by lazy {
        LocalBinder()
    }

    private val socket: Socket by lazy {
        IO.socket(BASE_URL, IO.Options.builder().setAuth(buildMap {
            val accessToken = sharedPreferences.getString(ACCESS_TOKEN, "")
            this@buildMap["token"] = accessToken
        }).build())
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        socket.connect()
        socket.on("chat") { data ->
            val message = gson.fromJson(data[0].toString(), Message::class.java)
            val process = ActivityManager.RunningAppProcessInfo()
            ActivityManager.getMyMemoryState(process)
            if (process.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                NotificationHelper.pushNotification(
                    applicationContext,
                    AppConstant.CHAT_CHANNEL_ID,
                    message.room,
                    "From ${message.userName}: ${message.content}"
                )
            } else {
                Log.i("INFO", "${message.room}: ${message.content}")
                messageLiveData.postValue(message)
            }
        }
        return START_STICKY
    }

    fun sendMessage(message: Message) {
        socket.emit("chat", gson.toJson(message))
    }

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods.
        fun getService(): ChatSocketService = this@ChatSocketService
    }
}