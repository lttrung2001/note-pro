package com.lttrung.notepro.ui.chat

import com.lttrung.notepro.database.data.networks.models.Message

interface ChatEvents {
    fun sendMessage(message: Message)
    fun sendAddNoteMessage(roomId: String)
    fun sendDeleteNoteMessage(roomId: String)
    fun sendAddMemberMessage(roomId: String, email: String)
    fun sendRemoveMemberMessage(roomId: String, email: String)
    fun getMessages(roomId: String, pageIndex: Int, limit: Int)
}