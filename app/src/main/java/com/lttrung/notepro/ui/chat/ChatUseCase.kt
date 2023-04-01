package com.lttrung.notepro.ui.chat

import com.lttrung.notepro.database.data.locals.entities.CurrentUser
import io.reactivex.rxjava3.core.Single
import io.socket.client.Socket
import javax.inject.Singleton

@Singleton
interface ChatUseCase {
    fun getCurrentUser(): Single<CurrentUser>
}