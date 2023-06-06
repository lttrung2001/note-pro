package com.lttrung.notepro.ui.incomingcall

import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lttrung.notepro.databinding.ActivityIncomingCallBinding
import com.lttrung.notepro.domain.data.networks.models.User
import com.lttrung.notepro.utils.AppConstant.Companion.MISSED_CALL_CHANNEL_ID
import com.lttrung.notepro.utils.AppConstant.Companion.ROOM_ID
import com.lttrung.notepro.utils.AppConstant.Companion.USER
import com.lttrung.notepro.utils.JitsiHelper
import com.lttrung.notepro.utils.NotificationHelper
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import org.jitsi.meet.sdk.JitsiMeetActivity

@AndroidEntryPoint
class IncomingCallActivity : AppCompatActivity() {
    private val binding: ActivityIncomingCallBinding by lazy {
        ActivityIncomingCallBinding.inflate(layoutInflater)
    }
    private val incomingCallViewModel: IncomingCallViewModel by viewModels()
    private val countDownTimer: CountDownTimer by lazy {
        object: CountDownTimer(30 * 1000, 1000) {
            override fun onTick(p0: Long) {

            }

            override fun onFinish() {
                ringtone.stop()
                finish()
                // Push notification for missing call
                val incomingUser = intent.getSerializableExtra(USER) as User?
                NotificationHelper.pushNotification(
                    this@IncomingCallActivity,
                    MISSED_CALL_CHANNEL_ID,
                    "Missed call",
                    "You missed call from ${incomingUser?.fullName}"
                )
            }

        }
    }
    private val ringtone: Ringtone by lazy {
        val defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        RingtoneManager.getRingtone(this@IncomingCallActivity, defaultRingtoneUri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        setListener()
        setObserver()
    }

    private fun setupView() {
        val incomingUser = intent.getSerializableExtra(USER) as User?
        incomingUser?.let { user ->
            binding.fullName.text = user.fullName
            // Play ringtone
            countDownTimer.start()
            ringtone.play()
        }
    }

    private fun setObserver() {
        incomingCallViewModel.currentUserLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    val currentUser = resource.data
                    val roomId = intent.getStringExtra(ROOM_ID)
                    if (roomId != null) {
                        val options = JitsiHelper.createOptions(roomId, currentUser)
                        JitsiMeetActivity.launch(this@IncomingCallActivity, options)
                        finish()
                    }
                }
                is Resource.Error -> {
                }
            }
        }
    }

    private fun setListener() {
        binding.buttonCallEnd.setOnClickListener {
            ringtone.stop()
            countDownTimer.cancel()
            finish()
        }
        binding.buttonAcceptCall.setOnClickListener {
            ringtone.stop()
            incomingCallViewModel.getCurrentUser()
        }
    }
}