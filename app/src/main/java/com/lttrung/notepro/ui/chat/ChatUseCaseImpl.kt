package com.lttrung.notepro.ui.chat

import com.lttrung.notepro.database.data.locals.entities.CurrentUser
import com.lttrung.notepro.database.repositories.UserRepositories
import javax.inject.Inject

class ChatUseCaseImpl @Inject constructor(
    private val userRepositories: UserRepositories
) : ChatUseCase {
    override fun getCurrentUserId(): String {
        return userRepositories.locals.getCurrentUserId()
    }
}