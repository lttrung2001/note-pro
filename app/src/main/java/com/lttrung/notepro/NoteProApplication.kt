package com.lttrung.notepro

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.lttrung.notepro.utils.AppConstant
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NoteProApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        registerChatChannel()
    }

    fun registerChatChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel.
            val name = "Chat channel"
            val descriptionText = "This channel use to notify chat message."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(AppConstant.CHAT_CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            val notificationManager =
                applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }
}
