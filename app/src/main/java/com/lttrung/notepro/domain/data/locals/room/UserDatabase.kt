package com.lttrung.notepro.domain.data.locals.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.lttrung.notepro.domain.data.locals.room.dao.CurrentUserDao
import com.lttrung.notepro.domain.data.locals.room.dao.NoteDao
import com.lttrung.notepro.domain.data.locals.room.entities.CurrentUser
import com.lttrung.notepro.domain.data.locals.room.entities.NoteLocalsModel
import com.lttrung.notepro.utils.AppConstant.Companion.USER_DATABASE_VERSION

@Database(
    entities = [CurrentUser::class, NoteLocalsModel::class],
    version = USER_DATABASE_VERSION
)
@TypeConverters()
abstract class UserDatabase : RoomDatabase() {
    abstract fun currentUserDao(): CurrentUserDao
    abstract fun noteDao(): NoteDao

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
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.beginTransaction()
                database.execSQL("DROP TABLE Note")
                database.execSQL("CREATE TABLE Note (" +
                        "id TEXT PRIMARY KEY NOT NULL, " +
                        "title TEXT NOT NULL, " +
                        "content TEXT NOT NULL, " +
                        "lastModified INTEGER NOT NULL, " +
                        "isPin INTEGER NOT NULL, " +
                        "isArchived INTEGER NOT NULL, " +
                        "isRemoved INTEGER NOT NULL, " +
                        "role TEXT NOT NULL)")
                database.setTransactionSuccessful()
                database.endTransaction()
            }
        }
    }
}