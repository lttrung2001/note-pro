package com.lttrung.notepro.domain.usecases

import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ChangePasswordUseCase {
    fun execute(oldPassword: String, newPassword: String): Single<String>
}