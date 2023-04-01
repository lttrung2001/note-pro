package com.lttrung.notepro.database.data.locals.impl

import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
import com.lttrung.notepro.database.data.locals.UserLocals
import com.lttrung.notepro.database.data.locals.entities.CurrentUser
import com.lttrung.notepro.database.data.locals.room.CurrentUserDao
import com.lttrung.notepro.utils.AppConstant.Companion.ACCESS_TOKEN
import com.lttrung.notepro.utils.AppConstant.Companion.REFRESH_TOKEN
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class UserLocalsImpl @Inject constructor(
    private val currentUserDao: CurrentUserDao,
    private val sharedPreferences: SharedPreferences
) : UserLocals {
    override fun login(currentUser: CurrentUser, refreshToken: String) {
        sharedPreferences.edit().putString(REFRESH_TOKEN, refreshToken).apply()
        currentUserDao.insertCurrentUser(currentUser)
    }

    override fun changePassword(password: String, refreshToken: String) {
        val currentUser = currentUserDao.getCurrentUser().blockingGet()
        currentUser.password = password
        currentUserDao.updateCurrentUser(currentUser)
        sharedPreferences.edit().putString(REFRESH_TOKEN, refreshToken).apply()
    }

    override fun changeProfile(fullName: String, phoneNumber: String) {
        val currentUser = currentUserDao.getCurrentUser().blockingGet()
        currentUser.fullName = fullName
        currentUser.phoneNumber = phoneNumber
        currentUserDao.updateCurrentUser(currentUser)
    }

    override fun fetchAccessToken(accessToken: String) {
        val decoded = JWT(accessToken)
        val currentUser = currentUserDao.getCurrentUser().blockingGet()
        currentUser.fullName = decoded.getClaim("name").asString().toString()
        currentUser.phoneNumber = decoded.getClaim("phone_number").asString().toString()
        currentUser.id = decoded.getClaim("user_id").asString().toString()
        currentUserDao.updateCurrentUser(currentUser)
        sharedPreferences.edit().putString(ACCESS_TOKEN, accessToken).apply()
    }

    override fun getCurrentUserInfo(): Single<CurrentUser> {
        return currentUserDao.getCurrentUser()
    }

    override fun getRefreshToken(): String {
        return sharedPreferences.getString(REFRESH_TOKEN, "")!!
    }

    override fun getAccessToken(): String {
        return sharedPreferences.getString(ACCESS_TOKEN, "")!!
    }

    override fun logout() {
        sharedPreferences.edit().remove(ACCESS_TOKEN).remove(REFRESH_TOKEN).apply()
        currentUserDao.deleteCurrentUser()
    }
}