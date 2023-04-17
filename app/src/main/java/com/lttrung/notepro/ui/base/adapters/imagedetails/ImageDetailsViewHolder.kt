package com.lttrung.notepro.ui.base.adapters.imagedetails

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.lttrung.notepro.R
import com.lttrung.notepro.domain.data.networks.models.ImageDetails
import com.lttrung.notepro.databinding.LayoutImageDetailsBinding
import com.lttrung.notepro.utils.Converter

class ImageDetailsViewHolder(
    private val binding: LayoutImageDetailsBinding
) : ViewHolder(binding.root) {

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