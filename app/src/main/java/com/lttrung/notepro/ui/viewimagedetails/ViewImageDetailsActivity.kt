package com.lttrung.notepro.ui.viewimagedetails

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.gson.Gson
import com.lttrung.notepro.databinding.ActivityViewImageDetailsBinding
import com.lttrung.notepro.domain.data.networks.models.Image
import com.lttrung.notepro.domain.data.networks.models.ImageDetails
import com.lttrung.notepro.domain.data.networks.models.User
import com.lttrung.notepro.ui.base.adapters.imagedetails.ImageDetailsAdapter
import com.lttrung.notepro.utils.AppConstant.Companion.IMAGES_JSON
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewImageDetailsActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityViewImageDetailsBinding.inflate(layoutInflater)
    }
    private val imageDetailsAdapter by lazy {
        val adapter = ImageDetailsAdapter()
        val images =
            Gson().fromJson(intent.getStringExtra(IMAGES_JSON), Array<Image>::class.java).toList()
                .map {
                    ImageDetails(
                        it.id,
                        it.name,
                        it.url,
                        it.uploadTime,
                        User(it.uploadBy, "Undefined")
                    )
                }
        adapter.submitList(images)
        adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    private fun initViews() {
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.images.let {
            it.adapter = imageDetailsAdapter
            PagerSnapHelper().attachToRecyclerView(it)
        }
    }
}