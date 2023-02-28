package com.lttrung.notepro.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.exceptions.ConnectivityException
import com.lttrung.notepro.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCase: MainUseCase
) : ViewModel() {
    val getNotes: MutableLiveData<Resource<List<Note>>> by lazy {
        MutableLiveData<Resource<List<Note>>>()
    }

    private var getNotesDisposable: Disposable? = null

    private val composite: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private val observerGetNotes: Consumer<List<Note>> by lazy {
        Consumer { data ->
            getNotes.postValue(Resource.Success(data))
        }
    }

    fun getNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            getNotes.postValue(Resource.Loading())
            getNotesDisposable?.let {
                composite.remove(it)
                it.dispose()
            }
            getNotesDisposable = useCase.getNotes().observeOn(AndroidSchedulers.mainThread())
                .subscribe(observerGetNotes, this@MainViewModel::getNotesError)
            getNotesDisposable?.let { composite.add(it) }
        }
    }

    private fun getNotesError(t: Throwable) {
        when (t) {
            is ConnectivityException -> {
                getNotes.postValue(Resource.Error(t.message ?: "Unknown error"))
            }
            else -> {
                getNotes.postValue(Resource.Error(t.message ?: "Unknown error"))
            }
        }
    }
}