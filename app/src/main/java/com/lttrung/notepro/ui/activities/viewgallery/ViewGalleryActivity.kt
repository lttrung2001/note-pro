package com.lttrung.notepro.ui.activities.viewgallery

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityViewGalleryBinding
import com.lttrung.notepro.ui.adapters.ImageSelectionAdapter
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.utils.AppConstant.Companion.PAGE_LIMIT
import com.lttrung.notepro.utils.AppConstant.Companion.SELECTED_IMAGES
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class ViewGalleryActivity : BaseActivity() {
    override val binding by lazy {
        ActivityViewGalleryBinding.inflate(layoutInflater)
    }
    private val viewModel: ViewGalleryViewModel by viewModels()
    private val imageSelectionAdapter by lazy {
        ImageSelectionAdapter()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getImages(this, imageSelectionAdapter.itemCount / PAGE_LIMIT, PAGE_LIMIT)
    }

    override fun initListeners() {
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
        viewModel.imagesLiveData.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    val images = resource.data.data
                    imageSelectionAdapter.submitList(images)
                }

                is Resource.Error -> {
                    Snackbar.make(
                        this,
                        binding.root,
                        resource.t.message.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun initViews() {
        binding.rvImages.adapter = imageSelectionAdapter
    }
}