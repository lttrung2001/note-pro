package com.lttrung.notepro.ui.login

import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface LoginUseCase {
    fun login(email: String, password: String): Single<String>
}