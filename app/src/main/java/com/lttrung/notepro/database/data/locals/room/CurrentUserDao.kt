package com.lttrung.notepro.database.data.locals.room

import androidx.room.*
import androidx.room.OnConflictStrategy.Companion.REPLACE
import com.lttrung.notepro.database.data.locals.entities.CurrentUser
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface CurrentUserDao {
    @Insert(onConflict = REPLACE)
    fun insertCurrentUser(currentUser: CurrentUser): Completable

    @Update(onConflict = REPLACE)
    fun updateCurrentUser(currentUser: CurrentUser): Completable

    @Delete
    fun deleteCurrentUser(currentUser: CurrentUser): Completable

    @Query("SELECT * FROM CurrentUser WHERE email = :email LIMIT 1")
    fun getCurrentUser(email: String): Single<CurrentUser>
}