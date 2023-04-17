package com.lttrung.notepro.ui.base.adapters.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.domain.data.networks.models.User
import com.lttrung.notepro.databinding.LayoutLoadingBinding
import com.lttrung.notepro.databinding.LayoutMyMessageBinding
import com.lttrung.notepro.databinding.LayoutOtherMessageBinding
import com.lttrung.notepro.ui.base.adapters.LoadingViewHolder

class MessageAdapter : ListAdapter<Message, ViewHolder>(CALLBACK) {
    var userId = ""

    fun showLoading() {
        val messages = currentList.toMutableList()
        messages.add(0, Message("", "", "", 0L, User("", "")))
        submitList(messages)
    }

    fun hideLoading(list: MutableList<Message>?) {
        list?.remove(list.find {
            it.id == ""
        })
        submitList(list)
    }

    fun removeLoadingElement() {
        val messages = currentList.toMutableList()
        messages.remove(currentList.find {
            it.id == ""
        })
        submitList(messages)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            MY_MESSAGE -> {
                val binding =
                    LayoutMyMessageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                MyMessageViewHolder(binding)
            }
            OTHER_MESSAGE -> {
                val binding =
                    LayoutOtherMessageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                OtherMessageViewHolder(binding)
            }
            else -> {
                val binding =
                    LayoutLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoadingViewHolder(binding)
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
            "" -> {
                LOADING
            }
            userId -> {
                MY_MESSAGE
            }
            else -> {
                OTHER_MESSAGE
            }
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

        private const val LOADING = 1
        private const val MY_MESSAGE = 2
        private const val OTHER_MESSAGE = 3
    }
}