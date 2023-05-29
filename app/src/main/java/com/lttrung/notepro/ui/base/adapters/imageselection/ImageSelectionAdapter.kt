package com.lttrung.notepro.ui.base.adapters.imageselection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.lttrung.notepro.databinding.LayoutImageSelectionBinding
import com.lttrung.notepro.domain.data.locals.models.ImageSelectionLocalsModel

class ImageSelectionAdapter :
    ListAdapter<ImageSelectionLocalsModel, ImageSelectionViewHolder>(itemCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSelectionViewHolder {
        val binding =
            LayoutImageSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageSelectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageSelectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val itemCallback = object : DiffUtil.ItemCallback<ImageSelectionLocalsModel>() {
            override fun areItemsTheSame(
                oldItem: ImageSelectionLocalsModel, newItem: ImageSelectionLocalsModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ImageSelectionLocalsModel, newItem: ImageSelectionLocalsModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}