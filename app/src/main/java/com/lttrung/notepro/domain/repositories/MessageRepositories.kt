package com.lttrung.notepro.domain.repositories

import com.lttrung.notepro.domain.data.networks.MessageNetworks
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.domain.data.networks.models.Theme
import io.socket.client.Socket
import javax.inject.Singleton

@Singleton
interface MessageRepositories {
    val networks: MessageNetworks
    suspend fun sendMessage(socket: Socket, message: Message)
    suspend fun sendAddNoteMessage(socket: Socket, roomId: String)
    suspend fun sendDeleteNoteMessage(socket: Socket, roomId: String)
    suspend fun sendAddMemberMessage(socket: Socket, roomId: String, email: String)
    suspend fun sendRemoveMemberMessage(socket: Socket, roomId: String, email: String)
    suspend fun call(socket: Socket, roomId: String)
    suspend fun changeTheme(socket: Socket, roomId: String, theme: Theme)

    suspend fun getMessages(roomId: String, pageIndex: Int, limit: Int): List<Message>
}