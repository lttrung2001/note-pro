package com.lttrung.notepro.ui.base.adapters.image

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.lttrung.notepro.R
import com.lttrung.notepro.domain.data.networks.models.Image
import com.lttrung.notepro.databinding.LayoutImageBinding

class ImageViewHolder(
    private val binding: LayoutImageBinding
) : ViewHolder(binding.root) {

    fun bind(image: Image, listener: ImagesAdapter.ImageListener) {
        binding.img.load(image.url) {
            crossfade(true)
            placeholder(R.drawable.me)
        }
        binding.root.setOnClickListener {
            // Start image details activity
            listener.onClick(image)
        }
        binding.buttonDeleteImg.setOnClickListener {
            listener.onDelete(image)
        }
    }
}