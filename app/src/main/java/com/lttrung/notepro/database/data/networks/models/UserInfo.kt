package com.lttrung.notepro.database.data.networks.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class UserInfo(
    @SerializedName("uid") val id: String,
    val email: String,
    val fullName: String,
    val phoneNumber: String
) : Serializable