package com.lttrung.notepro.domain.repositories.impl

import com.google.gson.Gson
import com.lttrung.notepro.domain.data.networks.MessageNetworks
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.domain.repositories.MessageRepositories
import io.socket.client.Socket
import javax.inject.Inject

class MessageRepositoriesImpl @Inject constructor(
    private val gson: Gson,
    override val networks: MessageNetworks
) : MessageRepositories {
    override suspend fun sendMessage(socket: Socket, message: Message) {
        socket.emit("chat", gson.toJson(message))
    }

    override suspend fun sendAddNoteMessage(socket: Socket, roomId: String) {
        socket.emit("add_note", roomId)
    }

    override suspend fun sendDeleteNoteMessage(socket: Socket, roomId: String) {
        socket.emit("delete_note", roomId)
    }

    override suspend fun sendAddMemberMessage(socket: Socket, roomId: String, email: String) {
        socket.emit("add_member", roomId, email)
    }

    override suspend fun sendRemoveMemberMessage(socket: Socket, roomId: String, email: String) {
        socket.emit("remove_member", roomId, email)
    }

    override suspend fun call(socket: Socket, roomId: String) {
        socket.emit("call", roomId)
    }

    override suspend fun getMessages(roomId: String, pageIndex: Int, limit: Int): List<Message> {
        return networks.fetchMessages(roomId, pageIndex, limit).data
    }
}