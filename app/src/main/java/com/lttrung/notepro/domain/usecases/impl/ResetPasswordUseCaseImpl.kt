package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.repositories.LoginRepositories
import com.lttrung.notepro.domain.usecases.ResetPasswordUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ResetPasswordUseCaseImpl @Inject constructor(
    private val repositories: LoginRepositories
) : ResetPasswordUseCase {
    override fun execute(code: String, newPassword: String): Single<Unit> {
        return repositories.resetPassword(code, newPassword)
    }
}