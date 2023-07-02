package com.lttrung.notepro.domain.data.locals.models

import com.lttrung.notepro.domain.data.networks.models.Image

data class MediaSelectionLocalsModel(
    override val id: String,
    override val name: String,
    override val url: String,
    override val uploadTime: Long,
    override val uploadBy: String,
    override val contentType: String,
    var isSelected: Boolean = false
) : Image(id, name, url, uploadTime, uploadBy), java.io.Serializable