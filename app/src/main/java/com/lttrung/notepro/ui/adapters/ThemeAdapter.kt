package com.lttrung.notepro.ui.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lttrung.notepro.databinding.ThemeItemBinding
import com.lttrung.notepro.domain.data.networks.models.Theme
import com.squareup.picasso.Picasso


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
        (holder as ThemeViewHolder).bind()
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
                Picasso.get().load(item.bgUrl).into(themeItemBackground)
                themeItemMyMessage.apply {
                    backgroundTintList = ColorStateList.valueOf(Color.parseColor(item.myMsgBgColor))
                    setTextColor(Color.parseColor(item.myMsgTextColor))
                }
            }
        }
    }
}