package com.lttrung.notepro.utils

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lttrung.notepro.R

object NotificationHelper {
    fun pushNotification(context: Context, channelId: String, title: String, content: String) {
        val notification = NotificationCompat.Builder(context, channelId).setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentText(content).setSmallIcon(R.drawable.app)
            .build()
        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), notification)
        }
    }
}