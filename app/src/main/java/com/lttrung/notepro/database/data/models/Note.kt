package com.lttrung.notepro.database.data.models

import java.io.Serializable

data class Note (
    val id: String,
    val title: String,
    val content: String,
    val lastModified: Int,
    val isPin: Boolean,
    val role: String,
    val images: List<Image>? = null
) : Serializable
