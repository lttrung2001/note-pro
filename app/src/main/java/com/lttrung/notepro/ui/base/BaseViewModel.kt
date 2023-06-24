package com.lttrung.notepro.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.withContext

open class BaseViewModel : ViewModel() {
    val isLoading by lazy {
        MutableLiveData(false)
    }
    val throwableLiveData by lazy {
        MutableLiveData<Throwable>()
    }
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        viewModelScope.launch {
            throwable.printStackTrace()
            withContext(Dispatchers.Main) {
                hideLoading()
                // Handle error
                throwableLiveData.postValue(throwable)
            }
        }
    }

    val network = viewModelScope + exceptionHandler

    fun launch(block: suspend CoroutineScope.() -> Unit): Job {
        showLoading()
        return network.launch(Dispatchers.IO) {
            block.invoke(network)
            withContext(Dispatchers.Main) {
                hideLoading()
            }
        }
    }

    private fun hideLoading() {
        isLoading.postValue(false)
    }

    private fun showLoading() {
        isLoading.postValue(true)
    }
}