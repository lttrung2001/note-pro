package com.lttrung.notepro.model

import java.io.Serializable

data class Note (
    val id: String,
    val title: String,
    val description: String,
    val isPin: Boolean
) : Serializable
