package com.lttrung.notepro

import android.app.Application
import android.app.NotificationManager
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_CHANNEL_ID
import com.lttrung.notepro.utils.AppConstant.Companion.CHAT_LISTENER_CHANNEL_ID
import com.lttrung.notepro.utils.AppConstant.Companion.MISSED_CALL_CHANNEL_ID
import com.lttrung.notepro.utils.CurrentActivityHolder
import com.lttrung.notepro.utils.NotificationChannelManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NoteProApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Register activity lifecycle callbacks
        registerActivityLifecycleCallbacks(CurrentActivityHolder())

        registerChatListenerChannel()
        registerChatChannel()
        registerMissedCallChannel()
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

    private fun registerMissedCallChannel() {
        val manager = NotificationChannelManager
        manager.registerChannel(
            this,
            MISSED_CALL_CHANNEL_ID,
            "Missed call channel",
            "This channel use to notify missed call.",
            NotificationManager.IMPORTANCE_HIGH
        )
    }
}
