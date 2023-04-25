package com.lttrung.notepro.domain.data.locals.impl

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
import com.lttrung.notepro.domain.data.locals.UserLocals
import com.lttrung.notepro.domain.data.locals.room.entities.CurrentUser
import com.lttrung.notepro.domain.data.locals.room.dao.CurrentUserDao
import com.lttrung.notepro.domain.data.locals.room.dao.NoteDao
import com.lttrung.notepro.ui.chat.ChatSocketService
import com.lttrung.notepro.utils.AppConstant.Companion.ACCESS_TOKEN
import com.lttrung.notepro.utils.AppConstant.Companion.REFRESH_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class UserLocalsImpl @Inject constructor(
    private val currentUserDao: CurrentUserDao,
    private val noteDao: NoteDao,
    private val sharedPreferences: SharedPreferences,
    @ApplicationContext private val context: Context
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

    override fun getCurrentUser(): Single<CurrentUser> {
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
        noteDao.deleteAllNotes()
        context.stopService(Intent(context, ChatSocketService::class.java))
    }
}