package com.lttrung.notepro.ui.changeprofile

import com.lttrung.notepro.database.data.networks.models.UserInfo
import com.lttrung.notepro.database.repositories.UserRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ChangeProfileUseCaseImpl @Inject constructor(
    private val repositories: UserRepositories
) : ChangeProfileUseCase {
    override fun changeProfile(fullName: String, phoneNumber: String): Single<UserInfo> {
        return repositories.changeProfile(fullName, phoneNumber)
    }
}