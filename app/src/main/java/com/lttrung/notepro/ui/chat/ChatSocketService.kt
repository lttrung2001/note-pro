package com.lttrung.notepro.ui.chat

import android.app.ActivityManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.lttrung.notepro.domain.data.locals.UserLocals
import com.lttrung.notepro.domain.data.networks.models.ApiResponse
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.domain.data.networks.models.User
import com.lttrung.notepro.domain.repositories.MessageRepositories
import com.lttrung.notepro.exceptions.InvalidTokenException
import com.lttrung.notepro.ui.incomingcall.IncomingCallActivity
import com.lttrung.notepro.ui.login.LoginActivity
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_LISTENER_CHANNEL_ID
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_LISTENER_NOTIFICATION_ID
import com.lttrung.notepro.utils.AppConstant.Companion.LOAD_MESSAGES_RECEIVED
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGES_JSON
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE_RECEIVED
import com.lttrung.notepro.utils.AppConstant.Companion.ROOM_ID
import com.lttrung.notepro.utils.AppConstant.Companion.USER
import com.lttrung.notepro.utils.CurrentActivityHolder
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
class ChatSocketService : Service() {
    @Inject
    lateinit var messageRepositories: MessageRepositories

    @Inject
    lateinit var userLocals: UserLocals

    @Inject
    lateinit var gson: Gson


    private lateinit var socket: Socket
    internal fun sendMessage(message: Message) {
        return messageRepositories.sendMessage(socket, message)
    }

    internal fun sendAddNoteMessage(roomId: String) {
        return messageRepositories.sendAddNoteMessage(socket, roomId)
    }

    internal fun sendDeleteNoteMessage(roomId: String) {
        return messageRepositories.sendDeleteNoteMessage(socket, roomId)
    }

    internal fun sendAddMemberMessage(roomId: String, email: String) {
        return messageRepositories.sendAddMemberMessage(socket, roomId, email)
    }

    internal fun sendRemoveMemberMessage(roomId: String, email: String) {
        return messageRepositories.sendRemoveMemberMessage(socket, roomId, email)
    }

    internal fun getMessages(roomId: String, pageIndex: Int, limit: Int) {
        return messageRepositories.getMessages(socket, roomId, pageIndex, limit)
    }

    internal fun call(roomId: String) {
        return messageRepositories.call(socket, roomId)
    }


    private val accessTokenLiveData: MutableLiveData<Resource<String>> by lazy {
        MutableLiveData<Resource<String>>()
    }


    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods.
        fun getService(): ChatSocketService = this@ChatSocketService
    }

    private val binder: Binder by lazy {
        LocalBinder()
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }


    /*
        This function use to start login page if faces some errors
     */
    private fun requireLogin() {
        userLocals.logout()
        val loginIntent = Intent(baseContext, LoginActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        baseContext.startActivity(loginIntent)
    }

    override fun onCreate() {
        super.onCreate()
        initObservers()
        val refreshToken = userLocals.getRefreshToken()
        if (refreshToken.isEmpty()) {
            accessTokenLiveData.postValue(Resource.Error(InvalidTokenException()))
        } else {
            callGetAccessTokenApi(refreshToken)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
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
                        resource.t.message.toString()
                    )
//                    requireLogin()
                    stopSelf()
                }
            }
        }
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
                accessTokenLiveData.postValue(Resource.Error(e))
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
        socket.on(Socket.EVENT_CONNECT_ERROR) {
            userLocals.logout()
            stopSelf()
        }
        socket.on("chat") { args ->
            val message = gson.fromJson(args[0].toString(), Message::class.java)
            val process = ActivityManager.RunningAppProcessInfo()
            ActivityManager.getMyMemoryState(process)
            val isChatActivity = CurrentActivityHolder.currentActivity is ChatActivity
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
        socket.on("call") { args ->
            val roomId = args[0] as String
            val userJson = args[1]
            val user = gson.fromJson(userJson.toString(), User::class.java)
            baseContext.startActivity(
                Intent(
                    this@ChatSocketService,
                    IncomingCallActivity::class.java
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(ROOM_ID, roomId)
                    putExtra(USER, user)
                }
            )
        }
    }
}