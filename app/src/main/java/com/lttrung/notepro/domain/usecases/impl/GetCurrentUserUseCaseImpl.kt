package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.data.locals.room.entities.CurrentUser
import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.domain.usecases.GetCurrentUserUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetCurrentUserUseCaseImpl @Inject constructor(
    private val userRepositories: UserRepositories
) : GetCurrentUserUseCase {
    override fun execute(): Single<CurrentUser> {
        return userRepositories.getCurrentUser()
    }
}