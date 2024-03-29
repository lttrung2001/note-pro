package com.lttrung.notepro.utils

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import com.lttrung.notepro.R
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.ui.activities.chat.ChatActivity
import com.lttrung.notepro.utils.AppConstant.Companion.MESSAGE
import com.lttrung.notepro.utils.AppConstant.Companion.NOTE
import com.lttrung.notepro.utils.AppConstant.Companion.ROOM_ID

object NotificationHelper {
    fun buildChatListenerNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, AppConstant.CHAT_LISTENER_CHANNEL_ID)
            .setContentTitle("Chat listener service")
            .setContentText("Service is running")
            .setSmallIcon(R.drawable.ic_app)
            .build()
    }

    fun pushNotification(
        context: Context, channelId: String, message: Message
    ) {
        val intent = Intent(context, ChatActivity::class.java).apply {
            putExtra(ROOM_ID, message.room)
            putExtra(NOTE, Note(message.room, ""))
            putExtra(MESSAGE, message)
//            flags =
//                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setPriority(NotificationCompat.PRIORITY_HIGH).setStyle(
                NotificationCompat.MessagingStyle(Person.Builder().setName("Me").build())
                    .setConversationTitle(message.room).addMessage(
                        message.content,
                        message.time,
                        Person.Builder().setName(message.user.fullName).build()
                    )
            ).setSmallIcon(R.drawable.ic_app).setContentIntent(pendingIntent).setAutoCancel(true)
            .build()
        with(NotificationManagerCompat.from(context)) {
            notify(message.room.hashCode(), notification)
        }
    }

    fun pushNotification(
        context: Context, channelId: String, title: String, content: String
    ) {
        val notification = NotificationCompat.Builder(context, channelId)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setChannelId(channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_app).setAutoCancel(true)
            .build()
        with(NotificationManagerCompat.from(context)) {
            notify(System.currentTimeMillis().toInt(), notification)
        }
    }
}