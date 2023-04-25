package com.lttrung.notepro.domain.data.networks.models

import java.io.Serializable

data class Message(
    val id: String,
    val content: String,
    val room: String,
    val time: Long,
    val user: User
) : Serializable
