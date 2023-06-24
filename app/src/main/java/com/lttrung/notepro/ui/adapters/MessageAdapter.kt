package com.lttrung.notepro.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.LoadingItemBinding
import com.lttrung.notepro.databinding.MyMessageItemBinding
import com.lttrung.notepro.databinding.OtherMessageItemBinding
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.domain.data.networks.models.User

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

    class MyMessageViewHolder(private val binding: MyMessageItemBinding) :
        ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.message.text = message.content
        }
    }

    class OtherMessageViewHolder(
        private val binding: OtherMessageItemBinding
    ) : ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.imgAvt.setImageResource(R.drawable.me)
            binding.name.text = message.user.fullName
            binding.message.text = message.content
        }
    }
}