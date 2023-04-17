package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.repositories.LoginRepositories
import com.lttrung.notepro.domain.usecases.LoginUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class LoginUseCaseImpl @Inject constructor(
    private val repositories: LoginRepositories
) : LoginUseCase {
    override fun execute(email: String, password: String): Single<String> {
        return repositories.login(email, password)
    }
}