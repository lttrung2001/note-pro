package com.lttrung.notepro.domain.repositories

import com.lttrung.notepro.domain.data.networks.models.Message
import io.socket.client.Socket
import javax.inject.Singleton

@Singleton
interface MessageRepositories {
    suspend fun sendMessage(socket: Socket, message: Message)
    suspend fun sendAddNoteMessage(socket: Socket, roomId: String)
    suspend fun sendDeleteNoteMessage(socket: Socket, roomId: String)
    suspend fun sendAddMemberMessage(socket: Socket, roomId: String, email: String)
    suspend fun sendRemoveMemberMessage(socket: Socket, roomId: String, email: String)
    suspend fun getMessages(socket: Socket, roomId: String, pageIndex: Int, limit: Int)
    suspend fun call(socket: Socket, roomId: String)
}