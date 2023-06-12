package com.lttrung.notepro.ui.activities.viewgallery

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.domain.data.locals.models.ImageSelectionLocalsModel
import com.lttrung.notepro.domain.data.networks.models.Paging
import com.lttrung.notepro.utils.GalleryUtils
import com.lttrung.notepro.utils.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class ViewGalleryViewModel : ViewModel() {
    internal val imagesLiveData by lazy {
        MutableLiveData<Resource<Paging<ImageSelectionLocalsModel>>>()
    }

    internal fun getImages(context: Context, page: Int, limit: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                imagesLiveData.postValue(Resource.Loading())
                val images = GalleryUtils.findImages(context, page, limit)
                imagesLiveData.postValue(Resource.Success(images))
            } catch (ex: Exception) {
                imagesLiveData.postValue(Resource.Error(ex))
            }
        }
    }
}