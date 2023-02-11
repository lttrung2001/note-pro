package com.lttrung.notepro.ui.forgotpassword

import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ForgotPasswordUseCase {
    fun forgotPassword(email: String): Single<Unit>
    fun verifyCode(code: String): Single<Unit>
    fun createNewPassword(newPassword: String, confirmPassword: String): Single<Unit>
}