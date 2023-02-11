package com.lttrung.notepro.ui.viewprofile

import com.lttrung.notepro.database.data.models.User
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ViewProfileUseCase {
    fun getProfile(): Single<User>
}