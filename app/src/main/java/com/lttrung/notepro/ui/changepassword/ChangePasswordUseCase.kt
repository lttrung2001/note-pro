package com.lttrung.notepro.ui.changepassword

import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ChangePasswordUseCase {
    fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String): Single<Unit>
}