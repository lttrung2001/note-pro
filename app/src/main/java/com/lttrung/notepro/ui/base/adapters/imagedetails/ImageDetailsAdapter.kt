package com.lttrung.notepro.ui.base.adapters.imagedetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.lttrung.notepro.domain.data.networks.models.ImageDetails
import com.lttrung.notepro.databinding.LayoutImageDetailsBinding

class ImageDetailsAdapter :
    ListAdapter<ImageDetails, ImageDetailsViewHolder>(ITEM_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageDetailsViewHolder {
        val binding =
            LayoutImageDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageDetailsViewHolder, position: Int) {
        val imageDetails = getItem(position)
        holder.bind(imageDetails)
    }

    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<ImageDetails>() {
            override fun areItemsTheSame(oldItem: ImageDetails, newItem: ImageDetails): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ImageDetails, newItem: ImageDetails): Boolean {
                return oldItem == newItem
            }
        }
    }
}