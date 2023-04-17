package com.lttrung.notepro.domain.data.networks.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserInfo(
    @SerializedName("uid") val id: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String
) : Serializable
