package com.lttrung.notepro.ui.chat

import android.app.ActivityManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.lttrung.notepro.NoteProApplication
import com.lttrung.notepro.database.data.locals.UserLocals
import com.lttrung.notepro.database.data.networks.models.ApiResponse
import com.lttrung.notepro.database.data.networks.models.Message
import com.lttrung.notepro.ui.login.LoginActivity
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_LISTENER_CHANNEL_ID
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_LISTENER_NOTIFICATION_ID
import com.lttrung.notepro.utils.AppConstant.Companion.LOAD_MESSAGES_RECEIVED
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGES_JSON
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE_RECEIVED
import com.lttrung.notepro.utils.NotificationHelper
import com.lttrung.notepro.utils.Resource
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
class ChatSocketService : Service(), ChatEvents {
    @Inject
    lateinit var userLocals: UserLocals

    @Inject
    lateinit var gson: Gson

    private val accessTokenLiveData: MutableLiveData<Resource<String>> by lazy {
        MutableLiveData<Resource<String>>()
    }

    private val noteProApplication: NoteProApplication by lazy {
        application as NoteProApplication
    }

    private val binder: Binder by lazy {
        LocalBinder()
    }

    private lateinit var socket: Socket
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
        initObservers()
        val refreshToken = userLocals.getRefreshToken()
        if (refreshToken.isEmpty()) {
            accessTokenLiveData.postValue(Resource.Error("Invalid refresh token"))
        } else {
            callGetAccessTokenApi(refreshToken)
        }
    }

    private fun initObservers() {
        accessTokenLiveData.observeForever { resource ->
            when (resource) {
                is Resource.Loading -> {

                }
                is Resource.Success -> {
                    val accessToken = resource.data
                    socket = createSocket(accessToken)
                    startConnection()
                    val chatListenerNotification =
                        NotificationHelper.buildChatListenerNotification(baseContext)
                    startForeground(CHAT_LISTENER_NOTIFICATION_ID, chatListenerNotification)
                }
                is Resource.Error -> {
                    NotificationHelper.pushNotification(
                        this,
                        CHAT_LISTENER_CHANNEL_ID,
                        "Error while connecting chat server",
                        resource.message
                    )
                    requireLogin()
                    stopSelf()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        noteProApplication.chatService = this@ChatSocketService
        return START_STICKY
    }

    override fun sendMessage(message: Message) {
        socket.emit("chat", gson.toJson(message))
    }

    override fun sendAddNoteMessage(roomId: String) {
        socket.emit("add_note", roomId)
    }

    override fun sendDeleteNoteMessage(roomId: String) {
        socket.emit("delete_note", roomId)
    }

    override fun sendAddMemberMessage(roomId: String, email: String) {
        socket.emit("add_member", roomId, email)
    }

    override fun sendRemoveMemberMessage(roomId: String, email: String) {
        socket.emit("remove_member", roomId, email)
    }

    override fun getMessages(roomId: String, pageIndex: Int, limit: Int) {
        socket.emit("load_messages", roomId, pageIndex, limit)
    }

    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods.
        fun getService(): ChatSocketService = this@ChatSocketService
    }

    private fun callGetAccessTokenApi(refreshToken: String) {
        val fetchAccessTokenThread = Thread {
            accessTokenLiveData.postValue(Resource.Loading())
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
                val accessToken =
                    Gson().fromJson(
                        response.body!!.string(),
                        ApiResponse::class.java
                    ).data as String
                accessTokenLiveData.postValue(Resource.Success(accessToken))
            } catch (e: Exception) {
                accessTokenLiveData.postValue(Resource.Error(e.message ?: "Unknown error"))
            }
        }
        fetchAccessTokenThread.start()
    }

    private fun createSocket(accessToken: String): Socket {
        return IO.socket(BASE_URL, IO.Options.builder().setReconnection(true).setAuth(buildMap {
            if (accessToken.isEmpty()) {
                requireLogin()
            }
            this["token"] = accessToken
        }).build())
    }

    private fun startConnection() {
        socket.connect()
        socket.on("chat") { args ->
            val message = gson.fromJson(args[0].toString(), Message::class.java)
            val process = ActivityManager.RunningAppProcessInfo()
            ActivityManager.getMyMemoryState(process)
            val isChatActivity = noteProApplication.chatActivity != null
            if (process.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || !isChatActivity) {
                NotificationHelper.pushNotification(
                    baseContext,
                    AppConstant.CHAT_CHANNEL_ID,
                    message
                )
            } else {
                // Send broadcast
                val messageReceivedIntent = Intent(MESSAGE_RECEIVED)
                messageReceivedIntent.putExtra(MESSAGE, message)
                sendBroadcast(messageReceivedIntent)
            }
        }
        socket.on("load_messages") { args ->
            // Using broadcast
            val olderMessagesJson = args[0].toString()
            val loadMessagesReceivedIntent = Intent(LOAD_MESSAGES_RECEIVED)
            loadMessagesReceivedIntent.putExtra(MESSAGES_JSON, olderMessagesJson)
            sendBroadcast(loadMessagesReceivedIntent)
        }
    }
}