package com.lttrung.notepro.ui.base.adapters.message

import androidx.recyclerview.widget.RecyclerView
import com.lttrung.notepro.database.data.locals.entities.Message
import com.lttrung.notepro.databinding.LayoutMyMessageBinding

class MyMessageViewHolder(private val binding: LayoutMyMessageBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(message: Message) {
        binding.message.text = message.content
    }
}