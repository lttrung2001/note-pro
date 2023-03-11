package com.lttrung.notepro.ui.resetpassword

import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ResetPasswordUseCase {
    fun resetPassword(code: String, newPassword: String): Single<Unit>
}