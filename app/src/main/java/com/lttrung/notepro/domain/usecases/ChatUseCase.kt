package com.lttrung.notepro.domain.usecases

import com.lttrung.notepro.domain.data.networks.models.Message
import io.socket.client.Socket
import javax.inject.Singleton

@Singleton
interface ChatUseCase {
    fun sendMessage(socket: Socket, message: Message)
    fun sendAddNoteMessage(socket: Socket, roomId: String)
    fun sendDeleteNoteMessage(socket: Socket, roomId: String)
    fun sendAddMemberMessage(socket: Socket, roomId: String, email: String)
    fun sendRemoveMemberMessage(socket: Socket, roomId: String, email: String)
    fun getMessages(socket: Socket, roomId: String, pageIndex: Int, limit: Int)
}