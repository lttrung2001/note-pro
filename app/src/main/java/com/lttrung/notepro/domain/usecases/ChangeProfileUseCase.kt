package com.lttrung.notepro.domain.usecases

import com.lttrung.notepro.domain.data.networks.models.UserInfo
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ChangeProfileUseCase {
    fun execute(fullName: String, phoneNumber: String): Single<UserInfo>
}