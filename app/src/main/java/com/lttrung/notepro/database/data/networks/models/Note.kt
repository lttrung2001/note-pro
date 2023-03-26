package com.lttrung.notepro.database.data.networks.models

import java.io.Serializable

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val lastModified: Long,
    val isPin: Boolean,
    val role: String,
    val images: List<Image>? = null
) : Serializable {
    fun hasEditPermission(): Boolean {
        return role == "owner" || role == "editor"
    }

    fun isOwner(): Boolean {
        return role == "owner"
    }
}
