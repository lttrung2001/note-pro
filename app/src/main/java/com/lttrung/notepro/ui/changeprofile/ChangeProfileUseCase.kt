package com.lttrung.notepro.ui.changeprofile

import com.lttrung.notepro.database.data.networks.models.UserInfo
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ChangeProfileUseCase {
    fun changeProfile(fullName: String, phoneNumber: String): Single<UserInfo>
}