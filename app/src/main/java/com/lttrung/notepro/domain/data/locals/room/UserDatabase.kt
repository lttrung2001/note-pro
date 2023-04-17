package com.lttrung.notepro.domain.data.locals.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import com.lttrung.notepro.utils.AppConstant.Companion.USER_DATABASE_VERSION

@Database(
    entities = [CurrentUser::class],
    version = USER_DATABASE_VERSION
)
abstract class UserDatabase : RoomDatabase() {
    abstract fun currentUserDao(): CurrentUserDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.beginTransaction()
                database.execSQL("ALTER TABLE CurrentUser ADD COLUMN fullName TEXT DEFAULT ''")
                database.execSQL("ALTER TABLE CurrentUser ADD COLUMN phoneNumber TEXT DEFAULT ''")
                database.setTransactionSuccessful()
                database.endTransaction()
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.beginTransaction()
                database.execSQL("ALTER TABLE CurrentUser ADD COLUMN id TEXT DEFAULT ''")
                database.setTransactionSuccessful()
                database.endTransaction()
            }
        }
    }
}