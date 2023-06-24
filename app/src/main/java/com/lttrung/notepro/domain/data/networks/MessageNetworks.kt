package com.lttrung.notepro.domain.data.networks

import com.lttrung.notepro.domain.data.networks.models.Message

interface MessageNetworks {
    suspend fun fetchMessages(roomId: String, pageIndex: Int, limit: Int): ResponseEntity<List<Message>>
}