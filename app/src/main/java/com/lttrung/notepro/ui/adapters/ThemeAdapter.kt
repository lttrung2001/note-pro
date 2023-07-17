package com.lttrung.notepro.ui.adapters

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lttrung.notepro.databinding.ThemeItemBinding
import com.lttrung.notepro.domain.data.networks.models.Theme

class ThemeAdapter(val callback: (theme: Theme) -> Unit) :
    ListAdapter<Theme, ViewHolder>(object : DiffUtil.ItemCallback<Theme>() {
        override fun areItemsTheSame(oldItem: Theme, newItem: Theme): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Theme, newItem: Theme): Boolean {
            return oldItem == newItem
        }

    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ThemeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ThemeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    inner class ThemeViewHolder(val binding: ThemeItemBinding) : ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                callback(getItem(bindingAdapterPosition))
            }
        }

        fun bind() {
            val item = getItem(bindingAdapterPosition)
            binding.apply {
                root.background = Drawable.createFromPath(item.bgUrl)
                themeItemOtherMessage.apply {
                    setBackgroundColor(Color.parseColor(item.otherMsgBgColor))
                    setTextColor(Color.parseColor(item.otherMsgTextColor))
                }
                themeItemMyMessage.apply {
                    setBackgroundColor(Color.parseColor(item.myMsgBgColor))
                    setTextColor(Color.parseColor(item.myMsgTextColor))
                }
            }
        }
    }
}