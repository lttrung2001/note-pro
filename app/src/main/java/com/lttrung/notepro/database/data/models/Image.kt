package com.lttrung.notepro.database.data.models

import java.io.Serializable

data class Image(
    val id: String,
    val name: String,
    val url: String,
    val uploadTime: Int,
    val uploadBy: Member
) : Serializable
