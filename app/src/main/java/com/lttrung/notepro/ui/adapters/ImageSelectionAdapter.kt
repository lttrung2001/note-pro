package com.lttrung.notepro.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ImageSelectionItemBinding
import com.lttrung.notepro.domain.data.locals.models.ImageSelectionLocalsModel

class ImageSelectionAdapter :
    ListAdapter<ImageSelectionLocalsModel, ImageSelectionAdapter.ImageSelectionViewHolder>(
        itemCallback
    ) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSelectionViewHolder {
        val binding =
            ImageSelectionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageSelectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageSelectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ImageSelectionViewHolder(private val binding: ImageSelectionItemBinding) :
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
}