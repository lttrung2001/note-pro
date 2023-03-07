package com.lttrung.notepro.ui.base.adapters.imageselection

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Image
import com.lttrung.notepro.databinding.LayoutImageBinding
import com.lttrung.notepro.databinding.LayoutImageSelectionBinding

class ImageSelectionViewHolder(private val binding: LayoutImageSelectionBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(image: Image) {
        binding.img.load(image.url) {
            crossfade(true)
            placeholder(R.drawable.me)
        }
        binding.checkbox.isChecked = false
    }
}