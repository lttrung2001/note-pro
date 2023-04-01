package com.lttrung.notepro.ui.setting

import com.lttrung.notepro.database.data.locals.UserLocals
import com.lttrung.notepro.database.data.locals.entities.CurrentUser
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class SettingUseCaseImpl @Inject constructor(
    private val userLocals: UserLocals
) : SettingUseCase {
    override fun getCurrentUserInfo(): Single<CurrentUser> {
        val currentUser = userLocals.getCurrentUserInfo()
        return currentUser
    }

    override fun logout(): Single<Unit> {
        return Single.just(userLocals.logout())
    }
}