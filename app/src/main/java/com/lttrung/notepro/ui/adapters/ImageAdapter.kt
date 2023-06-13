package com.lttrung.notepro.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ImageItemBinding
import com.lttrung.notepro.domain.data.networks.models.Image

class ImageAdapter(private val listener: ImageListener) :
    ListAdapter<Image, ImageAdapter.ImageViewHolder>(ITEM_CALLBACK) {
    companion object {
        private val ITEM_CALLBACK = object : DiffUtil.ItemCallback<Image>() {
            override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
                return oldItem == newItem
            }

        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = getItem(position)
        holder.bind(image, listener)
    }

    interface ImageListener {
        fun onClick(image: Image)
        fun onDelete(image: Image)
    }

    class ImageViewHolder(
        private val binding: ImageItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(image: Image, listener: ImageListener) {
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
}