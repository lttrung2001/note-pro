package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.repositories.LoginRepositories
import com.lttrung.notepro.domain.usecases.RegisterUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class RegisterUseCaseImpl @Inject constructor(
    private val repositories: LoginRepositories
) : RegisterUseCase {
    override fun execute(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String
    ): Single<Unit> {
        return repositories.register(email, password, fullName, phoneNumber)
    }
}