package com.lttrung.notepro.ui.viewgallery.adapters

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Image
import com.lttrung.notepro.databinding.LayoutImageBinding

class ImageSelectionViewHolder(private val binding: LayoutImageBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(image: Image) {
        binding.img.load(image.url) {
            crossfade(true)
            placeholder(R.drawable.me)
        }
        binding.checkbox.isChecked = false
    }
}