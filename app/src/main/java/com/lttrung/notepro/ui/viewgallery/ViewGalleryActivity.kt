package com.lttrung.notepro.ui.viewgallery

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lttrung.notepro.R
import com.lttrung.notepro.databinding.ActivityViewGalleryBinding
import com.lttrung.notepro.ui.base.adapters.imageselection.ImageSelectionAdapter
import com.lttrung.notepro.utils.AppConstant.Companion.PAGE_LIMIT
import com.lttrung.notepro.utils.AppConstant.Companion.SELECTED_IMAGES
import com.lttrung.notepro.utils.GalleryUtils
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class ViewGalleryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewGalleryBinding
    private lateinit var adapter: ImageSelectionAdapter
    private val viewModel: ViewGalleryViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        initObservers()
        viewModel.getImages(this, adapter.itemCount / PAGE_LIMIT, PAGE_LIMIT)
    }

    private fun initObservers() {
        viewModel.images.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {

                }

                is Resource.Success -> {
                    val images = resource.data.data
                    Log.i("INFO", images.toString())
                    adapter.submitList(images)
                }

                is Resource.Error -> {
                    Log.e("ERROR", resource.message)
                }
            }
        }
    }

    private fun initViews() {
        binding = ActivityViewGalleryBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)

        adapter = ImageSelectionAdapter()
        binding.rcvImages.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_select_images, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_select_images -> {
                val selectedImages = adapter.currentList.filter { image ->
                    image.isSelected
                }
                val resultIntent = Intent()
                resultIntent.putExtra(SELECTED_IMAGES, selectedImages as Serializable)
                setResult(RESULT_OK, resultIntent)
                finish()
                true
            }
            else -> {
                onBackPressed()
                true
            }
        }
    }
}