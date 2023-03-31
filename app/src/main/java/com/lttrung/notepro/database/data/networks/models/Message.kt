package com.lttrung.notepro.database.data.networks.models

import java.io.Serializable

data class Message(
    val id: String,
    val content: String,
    val room: String,
    val time: Long,
    val user: User
) : Serializable {
    companion object {

    }
}
