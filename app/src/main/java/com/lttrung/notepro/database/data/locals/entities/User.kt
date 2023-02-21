package com.lttrung.notepro.database.data.locals.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrentUser(
    @PrimaryKey val email: String,
    val password: String
)
