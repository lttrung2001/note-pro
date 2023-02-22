package com.lttrung.notepro.database.data.locals.impl

import com.lttrung.notepro.database.data.locals.UserLocals
import com.lttrung.notepro.database.data.locals.entities.CurrentUser
import io.reactivex.rxjava3.core.Single

class UserLocalsImpl : UserLocals{
    override fun login(currentUser: CurrentUser): Single<Unit> {
        TODO("Not yet implemented")
    }

    override fun changePassword(currentUser: CurrentUser): Single<Unit> {
        TODO("Not yet implemented")
    }

    override fun logout(currentUser: CurrentUser): Single<Unit> {
        TODO("Not yet implemented")
    }
}