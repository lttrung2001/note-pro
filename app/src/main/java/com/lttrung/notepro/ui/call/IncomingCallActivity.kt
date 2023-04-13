package com.lttrung.notepro.ui.call

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lttrung.notepro.databinding.ActivityIncomingCallBinding
import com.lttrung.notepro.utils.AppConstant.Companion.ROOM_ID
import com.lttrung.notepro.utils.JitsiHelper
import org.jitsi.meet.sdk.JitsiMeetActivity

class IncomingCallActivity : AppCompatActivity() {
    private val binding: ActivityIncomingCallBinding by lazy {
        ActivityIncomingCallBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setListener()
    }

    private fun setListener() {
        binding.buttonReject.setOnClickListener {
            finish()
        }
        binding.buttonAccept.setOnClickListener {
            val roomId = intent.getStringExtra(ROOM_ID)
            roomId?.let {
                val options = JitsiHelper.createOptions(roomId)
                JitsiMeetActivity.launch(this@IncomingCallActivity, options)
            }
        }
    }
}