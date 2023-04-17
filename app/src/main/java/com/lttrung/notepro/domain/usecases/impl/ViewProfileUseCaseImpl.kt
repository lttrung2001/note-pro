package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.data.networks.models.UserInfo
import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.domain.usecases.ViewProfileUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ViewProfileUseCaseImpl @Inject constructor(
    private val repositories: UserRepositories
) : ViewProfileUseCase {
    override fun execute(): Single<UserInfo> {
        return repositories.getProfile()
    }
}