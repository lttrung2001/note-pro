package com.lttrung.notepro.model

import java.io.Serializable

data class Member(
    val id: String,
    val email: String,
    val fullName: String,
    val role: Int
) : Serializable
