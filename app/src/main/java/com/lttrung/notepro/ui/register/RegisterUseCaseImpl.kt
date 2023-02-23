package com.lttrung.notepro.ui.register

import com.lttrung.notepro.database.repositories.UserRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class RegisterUseCaseImpl @Inject constructor(
    private val repositories: UserRepositories
) : RegisterUseCase {
    override fun register(
        email: String,
        password: String,
        fullName: String,
        phoneNumber: String
    ): Single<Unit> {
        return repositories.register(email, password, fullName, phoneNumber)
    }
}