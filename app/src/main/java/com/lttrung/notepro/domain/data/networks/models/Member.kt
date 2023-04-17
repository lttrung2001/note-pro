package com.lttrung.notepro.domain.data.networks.models

import java.io.Serializable

data class Member(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String,
    val phoneNumber: String
) : Serializable
