package com.lttrung.notepro.ui.base.adapters.message

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lttrung.notepro.R
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.databinding.LayoutOtherMessageBinding

data class OtherMessageViewHolder(
    private val binding: LayoutOtherMessageBinding
) : ViewHolder(binding.root) {
    fun bind(message: Message) {
        binding.imgAvt.setImageResource(R.drawable.me)
        binding.name.text = message.user.fullName
        binding.message.text = message.content
    }
}
