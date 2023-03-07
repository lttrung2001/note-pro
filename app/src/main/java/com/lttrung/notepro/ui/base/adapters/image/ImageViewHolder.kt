package com.lttrung.notepro.ui.base.adapters.image

import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Image
import com.lttrung.notepro.databinding.LayoutImageBinding

class ImageViewHolder (
    private val binding: LayoutImageBinding
        ) : ViewHolder(binding.root) {

    fun bind(image: Image) {
        binding.img.load(image.url) {
            crossfade(true)
            placeholder(R.drawable.me)
        }
    }
}