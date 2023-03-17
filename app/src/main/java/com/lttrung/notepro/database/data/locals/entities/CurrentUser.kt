package com.lttrung.notepro.database.data.locals.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrentUser(
    @PrimaryKey val email: String,
    var password: String,
    var fullName: String? = null,
    var phoneNumber: String? = null
)
