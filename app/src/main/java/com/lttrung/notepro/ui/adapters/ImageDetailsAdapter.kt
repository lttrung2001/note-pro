package com.lttrung.notepro.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ImageDetailsItemBinding
import com.lttrung.notepro.domain.data.networks.models.ImageDetails
import com.lttrung.notepro.utils.Converter

class ImageDetailsAdapter :
    ListAdapter<ImageDetails, ImageDetailsAdapter.ImageDetailsViewHolder>(ITEM_CALLBACK) {
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageDetailsViewHolder {
        val binding =
            ImageDetailsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageDetailsViewHolder, position: Int) {
        val imageDetails = getItem(position)
        holder.bind(imageDetails)
    }

    class ImageDetailsViewHolder(
        private val binding: ImageDetailsItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(imageDetails: ImageDetails) {
            binding.imgName.text = imageDetails.name
            binding.imgUploadBy.text = imageDetails.uploadBy.id
            binding.imgUploadTime.text = Converter.longToDate(imageDetails.uploadTime)
            binding.img.load(imageDetails.url) {
                crossfade(true)
                placeholder(R.drawable.me)
            }
        }
    }
}