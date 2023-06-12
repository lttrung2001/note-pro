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
import com.lttrung.notepro.ui.base.adapters.imageselection.ImageSelectionAdapter
import com.lttrung.notepro.utils.AppConstant.Companion.PAGE_LIMIT
import com.lttrung.notepro.utils.AppConstant.Companion.SELECTED_IMAGES
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class ViewGalleryActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityViewGalleryBinding.inflate(layoutInflater)
    }
    private val viewModel: ViewGalleryViewModel by viewModels()
    private val imageSelectionAdapter by lazy {
        ImageSelectionAdapter()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initObservers()
        viewModel.getImages(this, imageSelectionAdapter.itemCount / PAGE_LIMIT, PAGE_LIMIT)
    }

    private fun initObservers() {
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

    private fun initViews() {
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.rcvImages.adapter = imageSelectionAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_select_images, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_select_images -> {
                val selectedImages = imageSelectionAdapter.currentList.filter { image ->
                    image.isSelected
                }
                val resultIntent = Intent()
                resultIntent.putExtra(SELECTED_IMAGES, selectedImages as Serializable)
                setResult(RESULT_OK, resultIntent)
                finish()
            }
            else -> {
                onBackPressed()
            }
        }
        return true
    }
}