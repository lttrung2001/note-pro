package com.lttrung.notepro.ui.chat

import com.lttrung.notepro.database.data.locals.entities.CurrentUser
import javax.inject.Singleton

@Singleton
interface ChatUseCase {
    fun getCurrentUserId(): String
}