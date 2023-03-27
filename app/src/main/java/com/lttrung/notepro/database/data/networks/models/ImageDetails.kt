package com.lttrung.notepro.database.data.networks.models

import java.io.Serializable

data class ImageDetails(
    val id: String,
    val name: String,
    val url: String,
    val uploadTime: Long,
    val uploadBy: User
) : Serializable