package com.lttrung.notepro.domain.data.locals.models

import com.lttrung.notepro.domain.data.networks.models.Image

data class ImageSelectionLocalsModel(
    override val id: String,
    override val name: String,
    override val url: String,
    override val uploadTime: Long,
    override val uploadBy: String,
    var isSelected: Boolean = false
) : Image(id, name, url, uploadTime, uploadBy), java.io.Serializable