package com.lttrung.notepro.dto

import java.io.Serializable

data class Member(
    val id: String,
    val fullName: String,
    val email: String,
    val role: String
) : Serializable
