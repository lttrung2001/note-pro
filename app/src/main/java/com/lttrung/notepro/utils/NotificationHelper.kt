package com.lttrung.notepro.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.locals.entities.Message
import com.lttrung.notepro.ui.chat.ChatActivity
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE
import com.lttrung.notepro.utils.AppConstant.Companion.ROOM_ID

object NotificationHelper {
    fun pushNotification(
        context: Context,
        channelId: String,
        title: String,
        content: String,
        message: Message
    ) {
        val intent = Intent(context, ChatActivity::class.java).apply {
            putExtra(ROOM_ID, message.room)
            putExtra(MESSAGE, message)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, channelId).setContentTitle(title)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentText(content).setSmallIcon(R.drawable.app)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), notification)
        }
    }
}