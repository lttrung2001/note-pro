package com.lttrung.notepro.ui.viewgallery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lttrung.notepro.databinding.ActivityViewGalleryBinding
import com.lttrung.notepro.ui.base.adapters.imageselection.ImageSelectionAdapter
import com.lttrung.notepro.utils.AppConstant.Companion.PAGE_LIMIT
import com.lttrung.notepro.utils.GalleryUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewGalleryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewGalleryBinding
    private lateinit var adapter: ImageSelectionAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding = ActivityViewGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ImageSelectionAdapter()
        binding.rcvImages.adapter = adapter
        adapter.submitList(
            GalleryUtils.findImages(
                this,
                adapter.currentList.size / PAGE_LIMIT,
                PAGE_LIMIT
            ).blockingGet().data
        )
    }
}