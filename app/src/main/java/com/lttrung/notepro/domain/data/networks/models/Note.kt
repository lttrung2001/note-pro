package com.lttrung.notepro.domain.data.networks.models

import com.lttrung.notepro.domain.data.locals.entities.NoteLocalsModel
import java.io.Serializable

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val lastModified: Long,
    var isPin: Boolean,
    val role: String,
    var images: List<Image> = emptyList()
) : Serializable {
    fun hasEditPermission(): Boolean {
        return role == "owner" || role == "editor"
    }

    fun isOwner(): Boolean {
        return role == "owner"
    }

    fun toNoteLocalsModel(): NoteLocalsModel {
        return NoteLocalsModel(
            id,
            title,
            content,
            lastModified,
            isPin,
            role
        )
    }
}
