package com.lttrung.notepro.ui.viewimagedetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.google.gson.Gson
import com.lttrung.notepro.domain.data.networks.models.Image
import com.lttrung.notepro.domain.data.networks.models.ImageDetails
import com.lttrung.notepro.domain.data.networks.models.User
import com.lttrung.notepro.databinding.ActivityViewImageDetailsBinding
import com.lttrung.notepro.ui.base.adapters.imagedetails.ImageDetailsAdapter
import com.lttrung.notepro.utils.AppConstant.Companion.IMAGES_JSON
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewImageDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewImageDetailsBinding
    private val imageDetailsAdapter: ImageDetailsAdapter by lazy {
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
        setupView()
    }

    private fun setupView() {
        binding = ActivityViewImageDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.images.apply {
            layoutManager = LinearLayoutManager(
                this@ViewImageDetailsActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = imageDetailsAdapter
            PagerSnapHelper().attachToRecyclerView(this)
        }
    }
}