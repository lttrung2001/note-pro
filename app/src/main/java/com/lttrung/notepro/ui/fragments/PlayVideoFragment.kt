package com.lttrung.notepro.ui.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lttrung.notepro.databinding.FragmentPlayVideoBinding
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.utils.remove
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayVideoFragment(private val msg: Message) : BottomSheetDialogFragment(),
    MediaPlayer.OnPreparedListener {
    private val binding by lazy {
        FragmentPlayVideoBinding.inflate(layoutInflater)
    }
    private val mediaPlayer by lazy { MediaPlayer() }
    private lateinit var surfaceHolder: SurfaceHolder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        surfaceHolder = binding.content.holder
        surfaceHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(p0: SurfaceHolder) {
                mediaPlayer.apply {
                    setDataSource(msg.content)
                    setDisplay(p0)
                    binding.progressBar.show()
                    prepareAsync()
                }
            }

            override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

            }

            override fun surfaceDestroyed(p0: SurfaceHolder) {

            }

        })
        mediaPlayer.setOnPreparedListener(this)
        binding.content.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
            }
        }
        return binding.root
    }

    override fun onPrepared(p0: MediaPlayer?) {
        binding.progressBar.remove()
        p0?.start()
    }
}