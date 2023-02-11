package com.lttrung.notepro.model

import java.io.Serializable

data class Note (
    val id: String,
    val title: String,
    val content: String,
    val lastModified: Int,
    val isPin: Boolean,
    val role: String,
    val images: List<Image>?
) : Serializable
