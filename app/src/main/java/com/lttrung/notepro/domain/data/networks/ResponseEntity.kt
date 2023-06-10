package com.lttrung.notepro.domain.data.networks

import com.google.gson.annotations.SerializedName
import java.io.Serializable

open class ResponseEntity<T>(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T
) : Serializable