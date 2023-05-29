package com.lttrung.notepro.ui.base.adapters.imageselection

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.LayoutImageSelectionBinding
import com.lttrung.notepro.domain.data.locals.models.ImageSelectionLocalsModel

class ImageSelectionViewHolder(private val binding: LayoutImageSelectionBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(image: ImageSelectionLocalsModel) {
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