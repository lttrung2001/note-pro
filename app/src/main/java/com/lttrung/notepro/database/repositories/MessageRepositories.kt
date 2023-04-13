package com.lttrung.notepro.database.repositories

import com.lttrung.notepro.database.data.networks.models.Message
import io.socket.client.Socket
import javax.inject.Singleton

@Singleton
interface MessageRepositories {
    fun sendMessage(socket: Socket, message: Message)
    fun sendAddNoteMessage(socket: Socket, roomId: String)
    fun sendDeleteNoteMessage(socket: Socket, roomId: String)
    fun sendAddMemberMessage(socket: Socket, roomId: String, email: String)
    fun sendRemoveMemberMessage(socket: Socket, roomId: String, email: String)
    fun getMessages(socket: Socket, roomId: String, pageIndex: Int, limit: Int)
    fun call(socket: Socket, roomId: String)
}