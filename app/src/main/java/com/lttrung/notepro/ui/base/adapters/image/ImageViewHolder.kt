package com.lttrung.notepro.ui.base.adapters.image

import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lttrung.notepro.R
import com.lttrung.notepro.database.data.networks.models.Image

class ImageViewHolder (itemView: View) : ViewHolder(itemView) {
    private val img: ImageView = itemView.findViewById(R.id.img)

    fun bind(image: Image) {
        img.setImageURI(Uri.parse(image.url))
    }
}