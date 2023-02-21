package com.lttrung.notepro.database.data.networks.models

import java.io.Serializable

data class Member(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String
) : Serializable
