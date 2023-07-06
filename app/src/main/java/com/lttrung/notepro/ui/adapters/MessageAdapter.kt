package com.lttrung.notepro.ui.adapters

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.MyMessageItemBinding
import com.lttrung.notepro.databinding.OtherMessageItemBinding
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.remove
import com.lttrung.notepro.utils.show
import com.squareup.picasso.Picasso

class MessageAdapter : ListAdapter<Message, ViewHolder>(CALLBACK) {
    companion object {
        private val CALLBACK = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem == newItem
            }

        }
        private const val MY_MESSAGE = 2
        private const val OTHER_MESSAGE = 3
    }

    var userId = ""
    private var videoOnClick: ((msg: Message) -> Unit)? = null

    fun setVideoOnClick(handle: (msg: Message) -> Unit): MessageAdapter {
        videoOnClick = handle
        return this
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
        return when (message.user.id) {
            userId -> {
                MY_MESSAGE
            }

            else -> {
                OTHER_MESSAGE
            }
        }
    }

    inner class MyMessageViewHolder(private val binding: MyMessageItemBinding) :
        ViewHolder(binding.root) {
        fun bind(msg: Message) {
            when (msg.contentType) {
                AppConstant.MESSAGE_CONTENT_TYPE_TEXT -> {
                    binding.message.text = msg.content
                }

                AppConstant.MESSAGE_CONTENT_TYPE_IMAGE -> {
                    binding.apply {
                        message.remove()
                        image.show()
                        Picasso.get().load(msg.content).resize(160, 200).into(image)
                    }
                }

                AppConstant.MESSAGE_CONTENT_TYPE_VIDEO -> {
                    binding.apply {
                        message.remove()
                        contentVideo.apply {
                            show()
                            setOnClickListener {
                                videoOnClick?.let { onClick -> onClick(msg) }
                            }
                        }
                    }
                }
            }
        }
    }

    inner class OtherMessageViewHolder(
        private val binding: OtherMessageItemBinding
    ) : ViewHolder(binding.root) {
        private val mediaPlayer by lazy { MediaPlayer() }
        fun bind(msg: Message) {
            binding.apply {
                imgAvt.setImageResource(R.drawable.me)
                name.text = msg.user.fullName
                when (msg.contentType) {
                    AppConstant.MESSAGE_CONTENT_TYPE_TEXT -> {
                        message.text = msg.content
                    }

                    AppConstant.MESSAGE_CONTENT_TYPE_IMAGE -> {
                        message.remove()
                        contentImage.show()
                        Picasso.get().load(msg.content).resize(160, 200).into(contentImage)
                    }

                    AppConstant.MESSAGE_CONTENT_TYPE_VIDEO -> {
                        binding.apply {
                            message.remove()
                            mediaPlayer.apply {
                                setDataSource(msg.content)
                                setDisplay(contentVideo.holder)
                                prepareAsync()
                            }
                            contentVideo.apply {
                                show()
                                setOnClickListener {
                                    videoOnClick?.let { onClick -> onClick(msg) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}