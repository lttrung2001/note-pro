package com.lttrung.notepro.ui.activities.viewgallery

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.lttrung.notepro.databinding.ActivityViewGalleryBinding
import com.lttrung.notepro.ui.adapters.ImageSelectionAdapter
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.utils.AppConstant.Companion.PAGE_LIMIT
import com.lttrung.notepro.utils.AppConstant.Companion.SELECTED_IMAGES
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class ViewGalleryActivity : BaseActivity() {
    override val binding by lazy {
        ActivityViewGalleryBinding.inflate(layoutInflater)
    }
    override val viewModel: ViewGalleryViewModel by viewModels()
    private val imageSelectionAdapter by lazy {
        ImageSelectionAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getImages(this, imageSelectionAdapter.itemCount / PAGE_LIMIT, PAGE_LIMIT)
    }

    override fun initListeners() {
        super.initListeners()
        binding.btnSave.setOnClickListener {
            val selectedImages = imageSelectionAdapter.currentList.filter { image ->
                image.isSelected
            }
            val resultIntent = Intent()
            resultIntent.putExtra(SELECTED_IMAGES, selectedImages as Serializable)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    override fun initObservers() {
        super.initObservers()
        viewModel.imagesLiveData.observe(this) { paging ->
            val images = paging.data
            imageSelectionAdapter.submitList(images)
        }
    }

    override fun initViews() {
        super.initViews()
        binding.rvImages.adapter = imageSelectionAdapter
    }
}