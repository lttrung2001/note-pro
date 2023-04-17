package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.domain.usecases.LogoutUseCase
import javax.inject.Inject

class LogoutUseCaseImpl @Inject constructor(
    private val userRepositories: UserRepositories
) : LogoutUseCase {
    override fun execute() {
        return userRepositories.logout()
    }
}