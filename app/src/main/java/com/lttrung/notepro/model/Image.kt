package com.lttrung.notepro.model

import java.io.Serializable

data class Image(
    val id: String,
    val name: String,
    val url: String
) : Serializable
