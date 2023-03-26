package com.lttrung.notepro

import android.app.Application
import android.app.NotificationManager
import com.lttrung.notepro.services.ChatSocketService
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_CHANNEL_ID
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_LISTENER_CHANNEL_ID
import com.lttrung.notepro.utils.NotificationChannelManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NoteProApplication : Application() {
    var chatService: ChatSocketService? = null
    var isChatActivity: Boolean = false
    override fun onCreate() {
        super.onCreate()

        registerChatListenerChannel()
        registerChatChannel()
    }

    private fun registerChatListenerChannel() {
        val manager = NotificationChannelManager
        manager.registerChannel(
            this,
            CHAT_LISTENER_CHANNEL_ID,
            "Chat listener channel",
            "This service use to listen incoming message from background.",
            NotificationManager.IMPORTANCE_HIGH
        )
    }

    private fun registerChatChannel() {
        val manager = NotificationChannelManager
        manager.registerChannel(
            this,
            CHAT_CHANNEL_ID,
            "Chat channel",
            "This channel use to notify incoming message from background.",
            NotificationManager.IMPORTANCE_HIGH
        )
    }
}
