package com.lttrung.notepro.ui.chat

import com.lttrung.notepro.database.data.locals.entities.CurrentUser
import com.lttrung.notepro.database.data.networks.models.Message
import com.lttrung.notepro.database.repositories.MessageRepositories
import com.lttrung.notepro.database.repositories.UserRepositories
import io.reactivex.rxjava3.core.Single
import io.socket.client.Socket
import javax.inject.Inject

class ChatUseCaseImpl @Inject constructor(
    private val userRepositories: UserRepositories,
    private val messageRepositories: MessageRepositories
) : ChatUseCase {
    override fun getCurrentUser(): Single<CurrentUser> {
        return userRepositories.locals.getCurrentUserInfo()
    }

    override fun sendMessage(socket: Socket, message: Message) {
        return messageRepositories.sendMessage(socket, message)
    }

    override fun sendAddNoteMessage(socket: Socket, roomId: String) {
        return sendAddNoteMessage(socket, roomId)
    }

    override fun sendDeleteNoteMessage(socket: Socket, roomId: String) {
        return sendDeleteNoteMessage(socket, roomId)
    }

    override fun sendAddMemberMessage(socket: Socket, roomId: String, email: String) {
        return sendAddMemberMessage(socket, roomId, email)
    }

    override fun sendRemoveMemberMessage(socket: Socket, roomId: String, email: String) {
        return sendRemoveMemberMessage(socket, roomId, email)
    }

    override fun getMessages(socket: Socket, roomId: String, pageIndex: Int, limit: Int) {
        return messageRepositories.getMessages(socket, roomId, pageIndex, limit)
    }
}