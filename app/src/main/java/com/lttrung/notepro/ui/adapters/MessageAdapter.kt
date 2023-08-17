package com.lttrung.notepro.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ImageMyMessageItemBinding
import com.lttrung.notepro.databinding.ImageOtherMessageItemBinding
import com.lttrung.notepro.databinding.TextMyMessageItemBinding
import com.lttrung.notepro.databinding.TextOtherMessageItemBinding
import com.lttrung.notepro.databinding.VideoMyMessageItemBinding
import com.lttrung.notepro.databinding.VideoOtherMessageItemBinding
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.remove
import com.lttrung.notepro.utils.show
import com.squareup.picasso.Picasso

class MessageAdapter(
    val context: Context,
    val resource: Resources
) : RecyclerView.Adapter<ViewHolder>() {
    private val mList = mutableListOf<MediaMessage>()
    companion object {
        const val TEXT_MY_MESSAGE = 2
        const val IMAGE_MY_MESSAGE = 3
        const val VIDEO_MY_MESSAGE = 4
        const val TEXT_OTHER_MESSAGE = 5
        const val IMAGE_OTHER_MESSAGE = 6
        const val VIDEO_OTHER_MESSAGE = 7
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
        notifyItemRangeChanged(0, mList.size)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder.bindingAdapterPosition != -1) {
            val item = getItem(holder.bindingAdapterPosition)
            if (item.message.contentType == AppConstant.MESSAGE_CONTENT_TYPE_VIDEO) {
                if (holder is MyMessageVideoViewHolder) {
                    holder.binding.apply {
//                    playerView.player?.pause()
                        item.mPlayer.pause()
                        playbackButton.show()
                    }
                } else if (holder is OtherMessageVideoViewHolder) {
                    holder.binding.apply {
//                    playerView.player?.pause()
                        item.mPlayer.pause()
                        playbackButton.show()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            TEXT_MY_MESSAGE -> {
                MyMessageTextViewHolder(
                    TextMyMessageItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            IMAGE_MY_MESSAGE -> {
                MyMessageImageViewHolder(
                    ImageMyMessageItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIDEO_MY_MESSAGE -> {
                MyMessageVideoViewHolder(
                    VideoMyMessageItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            TEXT_OTHER_MESSAGE -> {
                OtherMessageTextViewHolder(
                    TextOtherMessageItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            IMAGE_OTHER_MESSAGE -> {
                OtherMessageImageViewHolder(
                    ImageOtherMessageItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIDEO_OTHER_MESSAGE -> {
                OtherMessageVideoViewHolder(
                    VideoOtherMessageItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                MyMessageTextViewHolder(
                    TextMyMessageItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TEXT_MY_MESSAGE -> (holder as MyMessageTextViewHolder).bind()
            IMAGE_MY_MESSAGE -> (holder as MyMessageImageViewHolder).bind()
            VIDEO_MY_MESSAGE -> (holder as MyMessageVideoViewHolder).bind()
            TEXT_OTHER_MESSAGE -> (holder as OtherMessageTextViewHolder).bind()
            IMAGE_OTHER_MESSAGE -> (holder as OtherMessageImageViewHolder).bind()
            VIDEO_OTHER_MESSAGE -> (holder as OtherMessageVideoViewHolder).bind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return when (message.message.user.id) {
            userId -> {
                when (message.message.contentType) {
                    AppConstant.MESSAGE_CONTENT_TYPE_TEXT -> TEXT_MY_MESSAGE
                    AppConstant.MESSAGE_CONTENT_TYPE_IMAGE -> IMAGE_MY_MESSAGE
                    AppConstant.MESSAGE_CONTENT_TYPE_VIDEO -> VIDEO_MY_MESSAGE
                    else -> TEXT_MY_MESSAGE
                }
            }

            else -> {
                when (message.message.contentType) {
                    AppConstant.MESSAGE_CONTENT_TYPE_TEXT -> TEXT_OTHER_MESSAGE
                    AppConstant.MESSAGE_CONTENT_TYPE_IMAGE -> IMAGE_OTHER_MESSAGE
                    AppConstant.MESSAGE_CONTENT_TYPE_VIDEO -> VIDEO_OTHER_MESSAGE
                    else -> TEXT_OTHER_MESSAGE
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun getItem(position: Int): MediaMessage {
        return mList[position]
    }

    fun addData(tmpList: List<MediaMessage>) {
        mList.addAll(0, tmpList)
        notifyItemRangeInserted(0, tmpList.size)
    }

    fun addSingleData(msg: MediaMessage) {
        mList.add(msg)
        notifyDataSetChanged()
    }

    fun onPause() {
        for (item in mList) {
            item.mPlayer.pause()
        }
    }

    fun onRelease() {
        for (item in mList) {
            item.mPlayer.release()
        }
    }

    inner class MyMessageTextViewHolder(val binding: TextMyMessageItemBinding) : ViewHolder(binding.root) {
        fun bind() {
            val msg = getItem(bindingAdapterPosition)
            binding.content.apply {
                backgroundTintList = ColorStateList.valueOf(bgColor)
                setTextColor(textColor)
                text = msg.message.content
            }
        }
    }

    inner class MyMessageImageViewHolder(val binding: ImageMyMessageItemBinding) : ViewHolder(binding.root) {
        fun bind() {
            val msg = getItem(bindingAdapterPosition)
            binding.img.apply {
                Picasso
                    .get()
                    .load(msg.message.content)
                    .placeholder(R.drawable.me)
                    .resize(160, 200)
                    .into(this)
            }
        }
    }

    inner class MyMessageVideoViewHolder(val binding: VideoMyMessageItemBinding) : ViewHolder(binding.root) {
        fun bind() {
            val msg = getItem(bindingAdapterPosition)
            binding.apply {
                playbackButton.imageTintList = ColorStateList.valueOf(bgColor)
                if (msg.mPlayer.currentMediaItem == null) {
//                            msg.mPlayer.setMediaItem(MediaItem.fromUri(msg.message.content))
                    msg.mPlayer.setMediaItem(MediaItem.fromUri("https://jsoncompare.org/LearningContainer/SampleFiles/Video/MP4/sample-mp4-file.mp4"))
                    msg.mPlayer.prepare()
                }
                playerView.player = msg.mPlayer
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

    inner class OtherMessageTextViewHolder(val binding: TextOtherMessageItemBinding) : ViewHolder(binding.root) {
        fun bind() {
            val msg = getItem(bindingAdapterPosition)
            binding.apply {
                imgAvt.setImageResource(R.drawable.me)
                name.text = msg.message.user.fullName
                message.text = msg.message.content
            }
        }
    }

    inner class OtherMessageImageViewHolder(val binding: ImageOtherMessageItemBinding) : ViewHolder(binding.root) {
        fun bind() {
            val msg = getItem(bindingAdapterPosition)
            binding.apply {
                imgAvt.setImageResource(R.drawable.me)
                name.text = msg.message.user.fullName
                Picasso
                    .get()
                    .load(msg.message.content)
                    .placeholder(R.drawable.me)
                    .resize(160, 200)
                    .into(contentImage)
            }
        }
    }

    inner class OtherMessageVideoViewHolder(val binding: VideoOtherMessageItemBinding) : ViewHolder(binding.root) {
        fun bind() {
            val msg = getItem(bindingAdapterPosition)
            binding.apply {
                imgAvt.setImageResource(R.drawable.me)
                name.text = msg.message.user.fullName
                playbackButton.imageTintList = ColorStateList.valueOf(bgColor)
                if (msg.mPlayer.currentMediaItem == null) {
                    msg.mPlayer.setMediaItem(MediaItem.fromUri(msg.message.content))
                    msg.mPlayer.prepare()
                }
                playerView.player = msg.mPlayer
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

    data class MediaMessage(val message: Message, val context: Context) {
        val mPlayer by lazy { ExoPlayer.Builder(context).build() }
    }
}