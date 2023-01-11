package com.lttrung.notepro.dto

import com.lttrung.notepro.model.Image
import java.io.Serializable

data class Note(
    val id: String,
    val title: String,
    val description: String,
    val isPin: Boolean,
    val images: List<Image>,
    val role: String
) : Serializable
