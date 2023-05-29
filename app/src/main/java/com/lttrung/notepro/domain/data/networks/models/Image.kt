package com.lttrung.notepro.domain.data.networks.models

import java.io.Serializable

data class Image(
    val id: String,
    val name: String,
    val url: String,
    val uploadTime: Long,
    val uploadBy: String
) : Serializable
