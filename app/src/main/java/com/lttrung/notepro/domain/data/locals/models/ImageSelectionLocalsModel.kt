package com.lttrung.notepro.domain.data.locals.models

data class ImageSelectionLocalsModel(
    val id: String,
    val name: String,
    val url: String,
    val uploadTime: Long,
    val uploadBy: String,
    var isSelected: Boolean = false
) : java.io.Serializable