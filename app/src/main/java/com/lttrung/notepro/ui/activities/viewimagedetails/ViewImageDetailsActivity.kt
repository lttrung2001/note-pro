package com.lttrung.notepro.ui.activities.viewimagedetails

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.PagerSnapHelper
import com.lttrung.notepro.databinding.ActivityViewImageDetailsBinding
import com.lttrung.notepro.domain.data.networks.models.ImageDetails
import com.lttrung.notepro.domain.data.networks.models.User
import com.lttrung.notepro.ui.adapters.ImageDetailsAdapter
import com.lttrung.notepro.ui.base.BaseActivity
import com.lttrung.notepro.ui.entities.ListImage
import com.lttrung.notepro.utils.AppConstant.Companion.LIST_IMAGE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewImageDetailsActivity : BaseActivity() {
    override val binding by lazy {
        ActivityViewImageDetailsBinding.inflate(layoutInflater)
    }
    private val imageDetailsAdapter by lazy {
        val adapter = ImageDetailsAdapter()
        val images = intent.getSerializableExtra(LIST_IMAGE) as ListImage?
        images?.let { listImage ->
            adapter.submitList(
                listImage.list.map {
                    ImageDetails(
                        it.id,
                        it.name,
                        it.url,
                        it.uploadTime,
                        User(it.uploadBy, "Undefined")
                    )
                })
        }
        adapter
    }

    override fun initViews() {
        super.initViews()
        binding.images.let {
            it.adapter = imageDetailsAdapter
            PagerSnapHelper().attachToRecyclerView(it)
        }
    }
}