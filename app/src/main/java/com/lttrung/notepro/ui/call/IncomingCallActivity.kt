package com.lttrung.notepro.ui.call

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lttrung.notepro.databinding.ActivityIncomingCallBinding
import com.lttrung.notepro.utils.AppConstant.Companion.ROOM_ID
import com.lttrung.notepro.utils.JitsiHelper
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import org.jitsi.meet.sdk.JitsiMeetActivity

@AndroidEntryPoint
class IncomingCallActivity : AppCompatActivity() {
    private val binding: ActivityIncomingCallBinding by lazy {
        ActivityIncomingCallBinding.inflate(layoutInflater)
    }
    private val incomingCallViewModel: IncomingCallViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setListener()
        setObserver()
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
                    }
                }
                is Resource.Error -> {
                }
            }
        }
    }

    private fun setListener() {
        binding.buttonReject.setOnClickListener {
            finish()
        }
        binding.buttonAccept.setOnClickListener {
            incomingCallViewModel.getCurrentUser()
        }
    }
}