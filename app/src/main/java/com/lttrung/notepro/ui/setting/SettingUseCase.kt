package com.lttrung.notepro.ui.setting

import com.lttrung.notepro.database.data.locals.entities.CurrentUser
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface SettingUseCase {
    fun getCurrentUserInfo(): Single<CurrentUser>
    fun logout(): Single<Unit>
}