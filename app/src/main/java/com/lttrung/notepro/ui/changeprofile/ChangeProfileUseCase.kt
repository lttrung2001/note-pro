package com.lttrung.notepro.ui.changeprofile

import com.lttrung.notepro.model.User
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ChangeProfileUseCase {
    fun changeProfile(user: User): Single<User>
}