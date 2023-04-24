package com.lttrung.notepro.domain.data.locals.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lttrung.notepro.domain.data.networks.models.Note

@Entity(tableName = "Note")
data class NoteLocalsModel(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val lastModified: Long,
    val isPin: Boolean,
    val isArchived: Boolean,
    val isRemoved: Boolean,
    val role: String,
) {
    fun toNoteNetworksModel(): Note {
        return Note(
            id,
            title,
            content,
            lastModified,
            isPin,
            isArchived,
            isRemoved,
            role
        )
    }
}
