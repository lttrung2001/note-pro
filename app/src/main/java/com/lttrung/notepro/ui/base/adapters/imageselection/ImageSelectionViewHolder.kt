package com.lttrung.notepro.ui.base.adapters.imageselection

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.locals.entities.Image
import com.lttrung.notepro.databinding.LayoutImageSelectionBinding

class ImageSelectionViewHolder(private val binding: LayoutImageSelectionBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(image: Image) {
        binding.img.load(image.url) {
            crossfade(true)
            placeholder(R.drawable.me)
        }
        binding.checkbox.isChecked = false
        binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            image.isSelected = isChecked
        }
        binding.root.setOnClickListener {
            image.isSelected = !image.isSelected
            binding.checkbox.isChecked = image.isSelected
        }
    }
}