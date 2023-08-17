package com.lttrung.notepro.ui.activities.jitsi

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.lttrung.notepro.R
import com.lttrung.notepro.ui.activities.chat.ChatSocketService
import com.lttrung.notepro.ui.dialogs.builders.DialogBuilder
import org.jitsi.meet.sdk.JitsiMeetActivity

class JitsiActivity : JitsiMeetActivity() {
    private val dialog by lazy {
        DialogBuilder(this)
            .setCanTouchOutside(false)
            .setNotice(R.string.ask_leave_meet)
            .addButtonLeft(R.string.agree) {
                finish()
            }
            .addButtonRight(R.string.cancel)
            .build()
    }
    lateinit var socketService: ChatSocketService
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as ChatSocketService.LocalBinder
            socketService = binder.getService()
            socketService.isInCall = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            socketService.isInCall = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, ChatSocketService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_IMPORTANT)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
    }

    override fun onBackPressed() {
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        socketService.isInCall = false
    }
}