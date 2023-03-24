package com.lttrung.notepro.ui.base.adapters.message

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lttrung.notepro.database.data.locals.entities.Message
import com.lttrung.notepro.databinding.LayoutMyMessageBinding
import com.lttrung.notepro.databinding.LayoutOtherMessageBinding

class MessageAdapter(
    private val userId: String
) : ListAdapter<Message, ViewHolder>(CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == MY_MESSAGE) {
            val binding =
                LayoutMyMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MyMessageViewHolder(binding)
        } else {
            val binding =
                LayoutOtherMessageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            OtherMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val type = getItemViewType(position)
        Log.i("INFO TYPE", type.toString())
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
        Log.i("INFO USER ID", userId)
        Log.i("INFO MESSAGE", message.toString())
        return if (message.userId == userId) {
            MY_MESSAGE
        } else {
            OTHER_MESSAGE
        }
    }

    companion object {
        private val CALLBACK = object : DiffUtil.ItemCallback<Message>() {
            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem == newItem
            }

        }

        private const val MY_MESSAGE = 1
        private const val OTHER_MESSAGE = 2
    }
}