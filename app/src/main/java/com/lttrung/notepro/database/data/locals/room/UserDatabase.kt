package com.lttrung.notepro.database.data.locals.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lttrung.notepro.database.data.locals.entities.CurrentUser
import com.lttrung.notepro.utils.AppConstant.Companion.USER_DATABASE_VERSION

@Database(entities = [CurrentUser::class], version = USER_DATABASE_VERSION)
abstract class UserDatabase : RoomDatabase() {
    abstract fun currentUserDao(): CurrentUserDao
}