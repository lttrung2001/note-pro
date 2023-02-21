package com.lttrung.notepro.ui.main

import com.lttrung.notepro.database.data.networks.models.Note
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface MainUseCase {
    fun getNotes(): Single<List<Note>>
}