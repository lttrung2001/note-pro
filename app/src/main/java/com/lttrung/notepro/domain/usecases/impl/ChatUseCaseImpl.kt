package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import com.lttrung.notepro.domain.data.networks.models.Message
import com.lttrung.notepro.domain.repositories.MessageRepositories
import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.domain.usecases.ChatUseCase
import io.reactivex.rxjava3.core.Single
import io.socket.client.Socket
import javax.inject.Inject

class ChatUseCaseImpl @Inject constructor(
    private val userRepositories: UserRepositories,
    private val messageRepositories: MessageRepositories
) : ChatUseCase {
    override fun getCurrentUser(): Single<CurrentUser> {
        return userRepositories.locals.getCurrentUser()
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