package com.lttrung.notepro.domain.data.networks

import com.lttrung.notepro.domain.data.networks.models.UserInfo
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface UserNetworks {
    fun changePassword(oldPassword: String, newPassword: String): Single<String>
    fun changeProfile(fullName: String, phoneNumber: String): Single<UserInfo>
    fun getProfile(): Single<UserInfo>
}