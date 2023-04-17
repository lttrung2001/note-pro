package com.lttrung.notepro.domain.repositories.impl

import com.google.gson.Gson
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.domain.repositories.MessageRepositories
import io.socket.client.Socket
import javax.inject.Inject

class MessageRepositoriesImpl @Inject constructor(
    private val gson: Gson
) : MessageRepositories {
    override fun sendMessage(socket: Socket, message: Message) {
        socket.emit("chat", gson.toJson(message))
    }

    override fun sendAddNoteMessage(socket: Socket, roomId: String) {
        socket.emit("add_note", roomId)
    }

    override fun sendDeleteNoteMessage(socket: Socket, roomId: String) {
        socket.emit("delete_note", roomId)
    }

    override fun sendAddMemberMessage(socket: Socket, roomId: String, email: String) {
        socket.emit("add_member", roomId, email)
    }

    override fun sendRemoveMemberMessage(socket: Socket, roomId: String, email: String) {
        socket.emit("remove_member", roomId, email)
    }

    override fun getMessages(socket: Socket, roomId: String, pageIndex: Int, limit: Int) {
        socket.emit("load_messages", roomId, pageIndex, limit)
    }

    override fun call(socket: Socket, roomId: String) {
        socket.emit("call", roomId)
    }
}