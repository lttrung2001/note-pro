package com.lttrung.notepro.ui.activities.chat

import android.app.ActivityManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.lttrung.notepro.domain.data.locals.UserLocals
import com.lttrung.notepro.domain.data.networks.ResponseEntity
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.domain.data.networks.models.Theme
import com.lttrung.notepro.domain.data.networks.models.User
import com.lttrung.notepro.domain.repositories.MessageRepositories
import com.lttrung.notepro.exceptions.InvalidTokenException
import com.lttrung.notepro.ui.activities.incomingcall.IncomingCallActivity
import com.lttrung.notepro.ui.activities.login.LoginActivity
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.AppConstant.Companion.CHANGE_THEME_RECEIVED
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_LISTENER_NOTIFICATION_ID
import com.lttrung.notepro.utils.AppConstant.Companion.IS_AUDIO_CALL
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE_RECEIVED
import com.lttrung.notepro.utils.AppConstant.Companion.ROOM_ID
import com.lttrung.notepro.utils.AppConstant.Companion.THEME
import com.lttrung.notepro.utils.AppConstant.Companion.USER
import com.lttrung.notepro.utils.CurrentActivityHolder
import com.lttrung.notepro.utils.NotificationHelper
import com.lttrung.notepro.utils.Resource
import com.lttrung.notepro.utils.RetrofitUtils.BASE_URL
import dagger.hilt.android.AndroidEntryPoint
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import javax.inject.Inject


@AndroidEntryPoint
class ChatSocketService : Service() {
    inner class LocalBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods.
        fun getService(): ChatSocketService = this@ChatSocketService
    }


    @Inject
    lateinit var messageRepositories: MessageRepositories

    @Inject
    lateinit var userLocals: UserLocals

    @Inject
    lateinit var gson: Gson

    var isInCall = false

    private var socket: Socket? = null

    private val scope = CoroutineScope(Dispatchers.IO)
    private val binder by lazy {
        LocalBinder()
    }
    private val accessTokenLiveData by lazy {
        MutableLiveData<Resource<String>>()
    }


    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    internal fun sendMessage(message: Message) {
        scope.launch {
            socket?.let { messageRepositories.sendMessage(it, message) }
        }
    }

    internal fun sendAddNoteMessage(roomId: String) {
        scope.launch {
            socket?.let { messageRepositories.sendAddNoteMessage(it, roomId) }
        }
    }

    internal fun sendDeleteNoteMessage(roomId: String) {
        scope.launch {
            socket?.let { messageRepositories.sendDeleteNoteMessage(it, roomId) }
        }
    }

    internal fun sendAddMemberMessage(roomId: String, email: String) {
        scope.launch {
            socket?.let { messageRepositories.sendAddMemberMessage(it, roomId, email) }
        }
    }

    internal fun sendRemoveMemberMessage(roomId: String, email: String) {
        scope.launch {
            socket?.let { messageRepositories.sendRemoveMemberMessage(it, roomId, email) }
        }
    }

    internal fun call(roomId: String) {
        scope.launch {
            socket?.let { messageRepositories.call(it, roomId, true) }
        }
    }

    internal fun callVideo(roomId: String) {
        scope.launch {
            socket?.let { messageRepositories.call(it, roomId) }
        }
    }

    internal fun changeTheme(roomId: String, theme: Theme) {
        scope.launch {
            socket?.let { messageRepositories.changeTheme(it, roomId, theme) }
        }
    }


    /*
        This function use to start login page if faces some errors
     */
    private fun switchToLoginScreen() {
        scope.launch(Dispatchers.IO) {
            userLocals.logout()
        }
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
                    startNotification()
                }

                is Resource.Error -> {
                    stopSelf()
                }
            }
        }
    }

    private fun startNotification() {
        val chatListenerNotification = NotificationHelper.buildChatListenerNotification(baseContext)
        startForeground(CHAT_LISTENER_NOTIFICATION_ID, chatListenerNotification)
    }

    private fun callGetAccessTokenApi(refreshToken: String) {
        scope.launch(Dispatchers.IO) {
            accessTokenLiveData.postValue(Resource.Loading())
            val client = OkHttpClient.Builder().build()
            // Create form data
            val formBody: RequestBody = FormBody.Builder().add("refreshToken", refreshToken).build()
            val request =
                Request.Builder().post(formBody).url(BASE_URL + "api/v1/get-access-token").build()
            try {
                // Send request to api server and wait for response
                val response = client.newCall(request).execute()
                // Convert response to object
                val accessToken = Gson().fromJson(
                    response.body!!.string(), ResponseEntity::class.java
                ).data as String
                accessTokenLiveData.postValue(Resource.Success(accessToken))
            } catch (e: Exception) {
                accessTokenLiveData.postValue(Resource.Error(e))
            }
        }
    }

    private fun createSocket(accessToken: String): Socket {
        return IO.socket(BASE_URL, IO.Options.builder()
            .setReconnection(true)
            .setSecure(false)
            .setAuth(buildMap {
            if (accessToken.isEmpty()) {
                switchToLoginScreen()
            }
            this["token"] = accessToken
        }).build())
    }

    private fun startConnection() {
        socket?.connect()
        socket?.on(Socket.EVENT_CONNECT_ERROR) {
            stopSelf()
        }
        if (socket != null) {
            listenChatEvent(socket!!)
            listenCallEvent(socket!!)
            listenChangeThemeEvent(socket!!)
        }
    }

    private fun listenCallEvent(socket: Socket) {
        socket.on("call") { args ->
            if (isInCall) {
                // Handle later...
            } else {
                handleConnectCall(
                    roomId = args[0].toString(),
                    userJson = args[1].toString(),
                    isAudioCall = args[2].toString().toBoolean()
                )
            }
        }
    }

    private fun handleConnectCall(
        roomId: String,
        userJson: String,
        isAudioCall: Boolean
    ) {
        val user = gson.fromJson(userJson, User::class.java)
        baseContext.startActivity(Intent(
            this@ChatSocketService,
            IncomingCallActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(ROOM_ID, roomId)
            putExtra(USER, user)
            putExtra(IS_AUDIO_CALL, isAudioCall)
        })
    }

    private fun listenChatEvent(socket: Socket) {
        socket.on("chat") { args ->
            val message = gson.fromJson(args[0].toString(), Message::class.java)
            val process = ActivityManager.RunningAppProcessInfo()
            ActivityManager.getMyMemoryState(process)
            val isChatActivity = CurrentActivityHolder.currentActivity is ChatActivity
            if (process.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND || !isChatActivity) {
                NotificationHelper.pushNotification(
                    baseContext, AppConstant.CHAT_CHANNEL_ID, message
                )
            } else {
                // Send broadcast
                val messageReceivedIntent = Intent(MESSAGE_RECEIVED)
                messageReceivedIntent.putExtra(MESSAGE, message)
                sendBroadcast(messageReceivedIntent)
            }
        }
    }

    private fun listenChangeThemeEvent(socket: Socket) {
        socket.on("change_theme") { args ->
            val theme = gson.fromJson(args[1].toString(), Theme::class.java)
            val process = ActivityManager.RunningAppProcessInfo()
            ActivityManager.getMyMemoryState(process)
            val isChatActivity = CurrentActivityHolder.currentActivity is ChatActivity
            if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && isChatActivity) {
                // Send broadcast
                val messageReceivedIntent = Intent(CHANGE_THEME_RECEIVED)
                messageReceivedIntent.putExtra(THEME, theme)
                sendBroadcast(messageReceivedIntent)
            }
        }
    }
}