package com.lttrung.notepro.domain.data.networks.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    @SerializedName("uid") val id: String,
    val fullName: String
) : Serializable
