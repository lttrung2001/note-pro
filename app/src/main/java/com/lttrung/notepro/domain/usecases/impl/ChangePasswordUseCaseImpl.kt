package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.domain.usecases.ChangePasswordUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ChangePasswordUseCaseImpl @Inject constructor(
    private val repositories: UserRepositories
) : ChangePasswordUseCase {
    override fun execute(
        oldPassword: String,
        newPassword: String
    ): Single<String> {
        return repositories.changePassword(oldPassword, newPassword)
    }
}