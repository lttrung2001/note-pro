package com.lttrung.notepro.ui.changepassword

import com.lttrung.notepro.database.repositories.UserRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ChangePasswordUseCaseImpl @Inject constructor(
    private val repositories: UserRepositories
) : ChangePasswordUseCase {
    override fun changePassword(
        oldPassword: String,
        newPassword: String
    ): Single<String> {
        return repositories.changePassword(oldPassword, newPassword)
    }
}