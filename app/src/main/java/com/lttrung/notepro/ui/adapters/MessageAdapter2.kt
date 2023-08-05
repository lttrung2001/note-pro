package com.lttrung.notepro.ui.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lttrung.notepro.databinding.MessageItemBinding
import com.lttrung.notepro.domain.data.networks.models.Note
import com.lttrung.notepro.utils.AppConstant
import com.squareup.picasso.Picasso

class MessageAdapter2(
    val onItemClick: (item: Note) -> Unit
) : ListAdapter<Note, MessageAdapter2.MessageViewHolder>(
    object : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = MessageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind()
    }

    inner class MessageViewHolder(val binding: MessageItemBinding) : ViewHolder(binding.root) {
        fun bind() {
            val note = getItem(bindingAdapterPosition)
            val message = note.lastMessage!!
            binding.apply {
                if (note.theme != null) {
                    Picasso.get()
                        .load(note.theme?.bgUrl)
                        .resize(60, 60)
                        .into(ivGroupImage)
                }
                tvGroupName.text = message.room
                tvLastMessage.text = when (message.contentType) {
                    AppConstant.MESSAGE_CONTENT_TYPE_IMAGE -> "Image"
                    AppConstant.MESSAGE_CONTENT_TYPE_VIDEO -> "Video"
                    else -> message.content
                }
                root.setOnClickListener {
                    onItemClick(note)
                }
            }
        }
    }
}