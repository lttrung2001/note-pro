package com.lttrung.notepro.ui.viewgallery

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.database.data.networks.models.Image
import com.lttrung.notepro.database.data.networks.models.Paging
import com.lttrung.notepro.utils.GalleryUtils
import com.lttrung.notepro.utils.Resource
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewGalleryViewModel : ViewModel() {
    val images: MutableLiveData<Resource<Paging<Image>>> by lazy {
        MutableLiveData<Resource<Paging<Image>>>()
    }

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var disposable: Disposable? = null

    private val observer: Consumer<Paging<Image>> by lazy {
        Consumer {
            images.postValue(Resource.Success(it))
        }
    }

    fun getImages(context: Context, page: Int, limit: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            images.postValue(Resource.Loading())
            disposable?.let { composite.remove(it) }
            disposable = GalleryUtils.findImages(context, page, limit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer) { t ->
                    images.postValue(Resource.Error(t.message ?: "Unknown error"))
                }
            disposable?.let { composite.add(it) }
        }
    }
}