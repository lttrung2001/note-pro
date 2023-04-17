package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.data.networks.models.UserInfo
import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.domain.usecases.ChangeProfileUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ChangeProfileUseCaseImpl @Inject constructor(
    private val repositories: UserRepositories
) : ChangeProfileUseCase {
    override fun execute(fullName: String, phoneNumber: String): Single<UserInfo> {
        return repositories.changeProfile(fullName, phoneNumber)
    }
}