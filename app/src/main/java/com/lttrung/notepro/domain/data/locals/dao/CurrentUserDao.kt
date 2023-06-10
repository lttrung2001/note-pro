package com.lttrung.notepro.domain.data.locals.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.lttrung.notepro.domain.data.locals.entities.CurrentUser

@Dao
interface CurrentUserDao {
    @Insert(onConflict = REPLACE)
    fun insertCurrentUser(currentUser: CurrentUser)

    @Update(onConflict = REPLACE)
    fun updateCurrentUser(currentUser: CurrentUser)

    @Query("DELETE FROM CurrentUser")
    fun deleteCurrentUser()

    @Query("SELECT * FROM CurrentUser LIMIT 1")
    fun getCurrentUser(): CurrentUser
}