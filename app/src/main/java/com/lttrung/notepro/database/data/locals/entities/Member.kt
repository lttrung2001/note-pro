package com.lttrung.notepro.database.data.locals.entities

import java.io.Serializable

data class Member(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String,
    val phoneNumber: String
) : Serializable
