package com.lttrung.notepro.ui.login

import com.lttrung.notepro.database.repositories.UserRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class LoginUseCaseImpl @Inject constructor(
    private val repositories: UserRepositories
) : LoginUseCase {
    override fun login(email: String, password: String): Single<String> {
        return repositories.login(email, password)
    }
}