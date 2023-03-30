package com.lttrung.notepro.ui.viewprofile

import com.lttrung.notepro.database.data.networks.models.UserInfo
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ViewProfileUseCase {
    fun getProfile(): Single<UserInfo>
}