package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.repositories.LoginRepositories
import com.lttrung.notepro.domain.usecases.ForgotPasswordUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ForgotPasswordUseCaseImpl @Inject constructor(
    private val repositories: LoginRepositories
) : ForgotPasswordUseCase {
    override fun execute(email: String): Single<Unit> {
        return repositories.forgotPassword(email)
    }
}