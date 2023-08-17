package com.lttrung.notepro.ui.activities.incomingcall

import android.media.RingtoneManager
import android.os.CountDownTimer
import androidx.activity.viewModels
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityIncomingCallBinding
import com.lttrung.notepro.domain.data.networks.models.User
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.utils.AppConstant.Companion.IS_AUDIO_CALL
import com.lttrung.notepro.utils.AppConstant.Companion.MISSED_CALL_CHANNEL_ID
import com.lttrung.notepro.utils.AppConstant.Companion.ROOM_ID
import com.lttrung.notepro.utils.AppConstant.Companion.USER
import com.lttrung.notepro.utils.JitsiHelper
import com.lttrung.notepro.utils.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IncomingCallActivity : BaseActivity() {
    override val binding by lazy {
        ActivityIncomingCallBinding.inflate(layoutInflater)
    }

    override val viewModel: IncomingCallViewModel by viewModels()
    private val incomingUser by lazy {
        intent.getSerializableExtra(USER) as User?
    }
    private val isAudioCall by lazy {
        intent.getBooleanExtra(IS_AUDIO_CALL, false)
    }
    private val countDownTimer by lazy {
        object : CountDownTimer(30 * 1000, 1000) {
            override fun onTick(p0: Long) {

            }

            override fun onFinish() {
                finish()
                // Push notification for missing call
                NotificationHelper.pushNotification(
                    this@IncomingCallActivity,
                    MISSED_CALL_CHANNEL_ID,
                    getString(R.string.missed_call),
                    getString(R.string.you_missed_call_from, incomingUser?.fullName)
                )
            }

        }
    }
    private val ringtone by lazy {
        val defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        RingtoneManager.getRingtone(this@IncomingCallActivity, defaultRingtoneUri)
    }

    override fun onDestroy() {
        super.onDestroy()
        ringtone.stop()
        countDownTimer.cancel()
    }

    override fun initViews() {
        super.initViews()
        if (!isAudioCall) {
            binding.buttonAcceptCall
                .setImageResource(R.drawable.ic_baseline_video_call_24)
        }
        incomingUser?.let { user ->
            binding.fullName.text = user.fullName
            // Play ringtone
            countDownTimer.start()
            ringtone.play()
        }
    }

    override fun initListeners() {
        super.initListeners()
        binding.apply {
            buttonCallEnd.setOnClickListener {
                ringtone.stop()
                countDownTimer.cancel()
                finish()
            }
            buttonAcceptCall.setOnClickListener {
                ringtone.stop()
                viewModel.getCurrentUser()
            }
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.currentUserLiveData.observe(this) { currentUser ->
            val roomId = intent.getStringExtra(ROOM_ID)
            if (roomId != null) {
                val options = JitsiHelper.createOptions(
                    roomId = roomId,
                    currentUser = currentUser,
                    audioOnly = isAudioCall
                )
                startJitsi(options)
                finish()
            }
        }
    }
}