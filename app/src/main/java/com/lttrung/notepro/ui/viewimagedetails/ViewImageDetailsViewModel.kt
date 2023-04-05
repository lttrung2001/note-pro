package com.lttrung.notepro.ui.viewimagedetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lttrung.notepro.database.data.networks.models.ImageDetails
import com.lttrung.notepro.utils.Resource
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer

class ViewImageDetailsViewModel : ViewModel() {
    val imagesLiveData: MutableLiveData<Resource<List<ImageDetails>>> by lazy {
        MutableLiveData<Resource<List<ImageDetails>>>()
    }
    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }
    private var imagesDisposable: Disposable? = null
    private val imagesObserver: Consumer<List<ImageDetails>> by lazy {
        Consumer {
            imagesLiveData.postValue(Resource.Success(it))
        }
    }
}