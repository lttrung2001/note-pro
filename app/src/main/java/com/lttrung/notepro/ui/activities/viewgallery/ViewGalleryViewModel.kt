package com.lttrung.notepro.ui.activities.viewgallery

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.lttrung.notepro.domain.data.locals.models.ImageSelectionLocalsModel
import com.lttrung.notepro.domain.data.networks.models.Paging
import com.lttrung.notepro.ui.base.BaseViewModel
import com.lttrung.notepro.utils.GalleryUtils
import com.lttrung.notepro.utils.Resource

class ViewGalleryViewModel : BaseViewModel() {
    internal val imagesLiveData by lazy {
        MutableLiveData<Paging<ImageSelectionLocalsModel>>()
    }

    internal fun getImages(context: Context, page: Int, limit: Int) {
        launch {
            val images = GalleryUtils.findImages(context, page, limit)
            imagesLiveData.postValue(images)
        }
    }
}