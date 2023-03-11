package com.lttrung.notepro.ui.resetpassword

import com.lttrung.notepro.database.repositories.LoginRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ResetPasswordUseCaseImpl @Inject constructor(
    private val repositories: LoginRepositories
) : ResetPasswordUseCase{
    override fun resetPassword(code: String, newPassword: String): Single<Unit> {
        return repositories.resetPassword(code, newPassword)
    }
}