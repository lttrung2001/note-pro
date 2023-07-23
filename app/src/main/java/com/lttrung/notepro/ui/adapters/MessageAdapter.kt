package com.lttrung.notepro.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.MyMessageItemBinding
import com.lttrung.notepro.databinding.OtherMessageItemBinding
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.remove
import com.lttrung.notepro.utils.show
import com.squareup.picasso.Picasso

class MessageAdapter(
    val context: Context,
    val resource: Resources
) : ListAdapter<MessageAdapter.MediaMessage, ViewHolder>(CALLBACK) {
    companion object {
        private val CALLBACK = object : DiffUtil.ItemCallback<MediaMessage>() {
            override fun areItemsTheSame(oldItem: MediaMessage, newItem: MediaMessage): Boolean {
                return oldItem.message.id == newItem.message.id
            }

            override fun areContentsTheSame(oldItem: MediaMessage, newItem: MediaMessage): Boolean {
                return oldItem == newItem
            }

        }
        private const val MY_MESSAGE = 2
        private const val OTHER_MESSAGE = 3
    }

    lateinit var userId: String
    private var bgColor = resource.getColor(R.color.primary, resource.newTheme())
    private var textColor = resource.getColor(R.color.white, resource.newTheme())

    @SuppressLint("ResourceAsColor")
    fun setPrimaryColor(bgColor: String?, textColor: String?) {
        if (bgColor != null) {
            this.bgColor = Color.parseColor(bgColor)
        }
        if (textColor != null) {
            this.textColor = Color.parseColor(textColor)
        }
        notifyItemRangeChanged(0, currentList.size)
    }

    override fun submitList(list: List<MediaMessage>?) {
        super.submitList(list?.toMutableList()?.map {
            it.context = context
            return@map it
        })
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        val item = getItem(holder.bindingAdapterPosition)
        if (item.message.contentType == AppConstant.MESSAGE_CONTENT_TYPE_VIDEO) {
            if (holder is MyMessageViewHolder) {
                item.mPlayer.pause()
                holder.binding.playbackButton.show()
            } else if (holder is OtherMessageViewHolder) {
                item.mPlayer.pause()
                holder.binding.playbackButton.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            MY_MESSAGE -> {
                val binding =
                    MyMessageItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                MyMessageViewHolder(binding)
            }

            else -> {
                val binding =
                    OtherMessageItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                OtherMessageViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val type = getItemViewType(position)
        if (type == MY_MESSAGE) {
            holder as MyMessageViewHolder
            holder.bind(getItem(position))
        } else if (type == OTHER_MESSAGE) {
            holder as OtherMessageViewHolder
            holder.bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return when (message.message.user.id) {
            userId -> {
                MY_MESSAGE
            }

            else -> {
                OTHER_MESSAGE
            }
        }
    }

    fun onPause() {
        for (item in currentList) {
            item.mPlayer.pause()
        }
    }

    fun onRelease() {
        for (item in currentList) {
            item.mPlayer.release()
        }
    }

    inner class MyMessageViewHolder(val binding: MyMessageItemBinding) :
        ViewHolder(binding.root) {

        @SuppressLint("ResourceAsColor")
        fun bind(msg: MediaMessage) {
            binding.message.backgroundTintList = ColorStateList.valueOf(bgColor)
            binding.message.setTextColor(textColor)
            when (msg.message.contentType) {
                AppConstant.MESSAGE_CONTENT_TYPE_TEXT -> {
                    binding.message.text = msg.message.content
                }

                AppConstant.MESSAGE_CONTENT_TYPE_IMAGE -> {
                    binding.apply {
                        message.remove()
                        image.show()
                        Picasso.get().load(msg.message.content).resize(160, 200).into(image)
                    }
                }

                AppConstant.MESSAGE_CONTENT_TYPE_VIDEO -> {
                    binding.apply {
                        playbackButton.imageTintList = ColorStateList.valueOf(bgColor)
                        message.remove()
                        if (msg.mPlayer.currentMediaItem == null) {
//                            msg.mPlayer.setMediaItem(MediaItem.fromUri(msg.message.content))
                            msg.mPlayer.setMediaItem(MediaItem.fromUri("https://jsoncompare.org/LearningContainer/SampleFiles/Video/MP4/sample-mp4-file.mp4"))
                            msg.mPlayer.prepare()
                        }
                        playerViewContainer.show()
                        playerView.apply {
                            player = msg.mPlayer
                            playerViewContainer.setOnClickListener {
                                if (msg.mPlayer.playWhenReady) {
                                    playbackButton.show()
                                    msg.mPlayer.pause()
                                } else {
                                    playbackButton.remove()
                                    msg.mPlayer.play()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    inner class OtherMessageViewHolder(
        val binding: OtherMessageItemBinding
    ) : ViewHolder(binding.root) {
        fun bind(msg: MediaMessage) {
            binding.apply {
                imgAvt.setImageResource(R.drawable.me)
                name.text = msg.message.user.fullName
                when (msg.message.contentType) {
                    AppConstant.MESSAGE_CONTENT_TYPE_TEXT -> {
                        message.text = msg.message.content
                    }

                    AppConstant.MESSAGE_CONTENT_TYPE_IMAGE -> {
                        message.remove()
                        contentImage.show()
                        Picasso.get().load(msg.message.content).resize(160, 200).into(contentImage)
                    }

                    AppConstant.MESSAGE_CONTENT_TYPE_VIDEO -> {
                        binding.apply {
                            playbackButton.imageTintList = ColorStateList.valueOf(bgColor)
                            message.remove()
                            if (msg.mPlayer.currentMediaItem == null) {
                                msg.mPlayer.setMediaItem(MediaItem.fromUri(msg.message.content))
                                msg.mPlayer.prepare()
                            }
                            playerViewContainer.show()
                            playerView.apply {
                                player = msg.mPlayer
                                playerViewContainer.setOnClickListener {
                                    if (msg.mPlayer.playWhenReady) {
                                        playbackButton.show()
                                        msg.mPlayer.pause()
                                    } else {
                                        playbackButton.remove()
                                        msg.mPlayer.play()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    data class MediaMessage(val message: Message) {
        val mPlayer by lazy { ExoPlayer.Builder(context).build() }
        var isChangeColor = false
        lateinit var context: Context
    }
}