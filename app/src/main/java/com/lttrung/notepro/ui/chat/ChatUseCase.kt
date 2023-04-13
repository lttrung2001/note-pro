package com.lttrung.notepro.ui.chat

import com.lttrung.notepro.database.data.locals.entities.CurrentUser
import com.lttrung.notepro.database.data.networks.models.Message
import io.reactivex.rxjava3.core.Single
import io.socket.client.Socket
import javax.inject.Singleton

@Singleton
interface ChatUseCase {
    fun getCurrentUser(): Single<CurrentUser>
    fun sendMessage(socket: Socket, message: Message)
    fun sendAddNoteMessage(socket: Socket, roomId: String)
    fun sendDeleteNoteMessage(socket: Socket, roomId: String)
    fun sendAddMemberMessage(socket: Socket, roomId: String, email: String)
    fun sendRemoveMemberMessage(socket: Socket, roomId: String, email: String)
    fun getMessages(socket: Socket, roomId: String, pageIndex: Int, limit: Int)
}