package com.lttrung.notepro.database.data.locals.impl

import android.content.SharedPreferences
import android.util.Log
import com.lttrung.notepro.database.data.locals.UserLocals
import com.lttrung.notepro.database.data.locals.entities.CurrentUser
import com.lttrung.notepro.database.data.locals.room.CurrentUserDao
import com.lttrung.notepro.utils.AppConstant.Companion.ACCESS_TOKEN
import com.lttrung.notepro.utils.AppConstant.Companion.REFRESH_TOKEN
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class UserLocalsImpl @Inject constructor(
    private val currentUserDao: CurrentUserDao,
    private val sharedPreferences: SharedPreferences
) : UserLocals {
    override fun login(currentUser: CurrentUser, refreshToken: String): Completable {
        sharedPreferences.edit().putString(REFRESH_TOKEN, refreshToken).apply()
        return currentUserDao.insertCurrentUser(currentUser)
    }

    override fun changePassword(currentUser: CurrentUser): Single<Unit> {
        TODO("Not yet implemented")
    }

    override fun logout(currentUser: CurrentUser): Single<Unit> {
        TODO("Not yet implemented")
    }
}