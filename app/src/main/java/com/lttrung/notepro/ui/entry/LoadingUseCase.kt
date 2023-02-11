package com.lttrung.notepro.ui.entry

import com.lttrung.notepro.database.data.models.Note
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface LoadingUseCase {
    fun getNotes(): Single<List<Note>>
}