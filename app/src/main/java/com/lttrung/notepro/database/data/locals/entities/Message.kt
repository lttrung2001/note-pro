package com.lttrung.notepro.database.data.locals.entities

data class Message(
    val id: String,
    val userId: String,
    val userName: String,
    val content: String,
    val room: String
)
