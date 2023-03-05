package com.lttrung.notepro.ui.viewgallery.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.lttrung.notepro.database.data.networks.models.Image
import com.lttrung.notepro.databinding.LayoutImageBinding

class ImageSelectionAdapter : ListAdapter<Image, ImageSelectionViewHolder>(itemCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSelectionViewHolder {
        val binding = LayoutImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageSelectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageSelectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val itemCallback = object : DiffUtil.ItemCallback<Image>() {
            override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
                return oldItem == newItem
            }

        }
    }
}