package com.lttrung.notepro.ui.setting

import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface SettingUseCase {
    fun logout(email: String): Single<Unit>
}