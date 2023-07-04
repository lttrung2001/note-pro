package com.lttrung.notepro.ui.adapters

import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.lttrung.notepro.databinding.ImageSelectionItemBinding
import com.lttrung.notepro.domain.data.locals.models.MediaSelectionLocalsModel
import com.lttrung.notepro.utils.AppConstant
import com.lttrung.notepro.utils.remove
import com.lttrung.notepro.utils.toByteArray
import com.squareup.picasso.Picasso
import java.io.File

class MediaSelectionAdapter :
    ListAdapter<MediaSelectionLocalsModel, MediaSelectionAdapter.ImageSelectionViewHolder>(
        itemCallback
    ) {
    companion object {
        private val itemCallback = object : DiffUtil.ItemCallback<MediaSelectionLocalsModel>() {
            override fun areItemsTheSame(
                oldItem: MediaSelectionLocalsModel, newItem: MediaSelectionLocalsModel
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: MediaSelectionLocalsModel, newItem: MediaSelectionLocalsModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    private var isSelectSingle = false
    private var itemListener: ItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSelectionViewHolder {
        val binding =
            ImageSelectionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageSelectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageSelectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setIsSelectSingle(b: Boolean): MediaSelectionAdapter {
        isSelectSingle = b
        return this
    }

    fun setItemListener(itemListener: ItemListener): MediaSelectionAdapter {
        this.itemListener = itemListener
        return this
    }

    interface ItemListener {
        fun onClick(image: MediaSelectionLocalsModel)
    }

    inner class ImageSelectionViewHolder(private val binding: ImageSelectionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: MediaSelectionLocalsModel) {
            when (image.contentType) {
                AppConstant.MESSAGE_CONTENT_TYPE_IMAGE -> {
                    Picasso.get().load(File(image.url)).resize(300, 400).into(binding.img)
                }

                AppConstant.MESSAGE_CONTENT_TYPE_VIDEO -> {
                    val bitmap = ThumbnailUtils.createVideoThumbnail(
                        image.url,
                        MediaStore.Images.Thumbnails.MINI_KIND
                    )
                    binding.img.setImageBitmap(Bitmap.createScaledBitmap(bitmap!!, 300, 400, false))
                }
            }
            if (!isSelectSingle) {
                binding.checkbox.isChecked = image.isSelected
                binding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                    image.isSelected = isChecked
                }
                binding.root.setOnClickListener {
                    image.isSelected = !image.isSelected
                    binding.checkbox.isChecked = image.isSelected
                }
            } else {
                binding.checkbox.remove()
                binding.root.setOnClickListener {
                    itemListener?.onClick(image)
                }
            }
        }
    }
}