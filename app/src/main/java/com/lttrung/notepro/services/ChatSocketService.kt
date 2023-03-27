package com.lttrung.notepro.services

import android.app.ActivityManager
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.lttrung.notepro.NoteProApplication
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Message
import com.lttrung.notepro.database.data.networks.models.ApiResponse
import com.lttrung.notepro.exceptions.InvalidTokenException
import com.lttrung.notepro.ui.login.LoginActivity
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_LISTENER_CHANNEL_ID
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_LISTENER_NOTIFICATION_ID
import com.lttrung.notepro.utils.AppConstant.Companion.REFRESH_TOKEN
import com.lttrung.notepro.utils.NotificationHelper
import com.lttrung.notepro.utils.RetrofitUtils.BASE_URL
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import javax.inject.Inject


@AndroidEntryPoint
class ChatSocketService : Service() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var gson: Gson

    val messageLiveData = MutableLiveData<Message>()

    private val binder: Binder by lazy {
        LocalBinder()
    }

    private val socket: Socket by lazy {
        IO.socket(BASE_URL, IO.Options.builder().setReconnection(true).setAuth(buildMap {
            val refreshToken = sharedPreferences.getString(REFRESH_TOKEN, "")
            if (refreshToken.isNullOrEmpty()) {
                requireLogin()
            }
            try {
                val accessToken = callGetAccessTokenApi(refreshToken!!)
                if (accessToken.isEmpty()) {
                    throw InvalidTokenException()
                }
                this["token"] = accessToken
            } catch (e: Exception) {
                requireLogin()
            }
        }).build())
    }

    private fun requireLogin() {
        val loginIntent = Intent(baseContext, LoginActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        baseContext.startActivity(loginIntent)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        val notification: Notification =
            NotificationCompat.Builder(baseContext, CHAT_LISTENER_CHANNEL_ID)
                .setContentTitle("Chat listener service")
                .setContentText("Service is running")
                .setSmallIcon(R.drawable.app)
                .build()
        startForeground(CHAT_LISTENER_NOTIFICATION_ID, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        socket.connect()
        socket.on("chat") { args ->
            val message = gson.fromJson(args[0].toString(), Message::class.java)
            val process = ActivityManager.RunningAppProcessInfo()
            ActivityManager.getMyMemoryState(process)
            val isChatActivity = (application as NoteProApplication).isChatActivity
            if (process.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || !isChatActivity) {
                NotificationHelper.pushNotification(
                    applicationContext,
                    AppConstant.CHAT_CHANNEL_ID,
                    message.room,
                    "From ${message.user.fullName}: ${message.content}",
                    message
                )
            } else {
                messageLiveData.postValue(message)
            }
        }
        (application as NoteProApplication).chatService = this@ChatSocketService
        return START_STICKY
    }

    fun sendMessage(message: Message) {
        socket.emit("chat", gson.toJson(message))
    }

    fun sendAddNoteMessage(roomId: String) {
        socket.emit("add_note", roomId)
    }

    fun sendDeleteNoteMessage(roomId: String) {
        socket.emit("delete_note", roomId)
    }

    fun sendAddMemberMessage(roomId: String, email: String) {
        socket.emit("add_member", roomId, email)
    }

    fun sendRemoveMemberMessage(roomId: String, email: String) {
        socket.emit("remove_member", roomId, email)
    }

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods.
        fun getService(): ChatSocketService = this@ChatSocketService
    }

    private fun callGetAccessTokenApi(refreshToken: String): String {
        var accessToken = ""
        val thread = Thread {
            val client = OkHttpClient.Builder()
                .build()
            // Create form data
            val formBody: RequestBody = FormBody.Builder().add("refreshToken", refreshToken).build()
            val request =
                Request.Builder().post(formBody).url(BASE_URL + "api/v1/get-access-token")
                    .build()
            try {
                // Send request to api server and wait for response
                val response = client.newCall(request).execute()
                // Convert response to object
                accessToken =
                    Gson().fromJson(
                        response.body!!.string(),
                        ApiResponse::class.java
                    ).data as String
            } catch (e: Exception) {
                throw e
            }
        }
        // Start thread
        thread.start()
        // Wait until thread die to get final response
        thread.join()
        return accessToken
    }
}