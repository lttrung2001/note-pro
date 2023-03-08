package com.lttrung.notepro.ui.viewprofile

import com.lttrung.notepro.database.data.networks.models.User
import com.lttrung.notepro.database.repositories.UserRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ViewProfileUseCaseImpl @Inject constructor(
    private val repositories: UserRepositories
) : ViewProfileUseCase {
    override fun getProfile(): Single<User> {
        return repositories.getProfile()
    }
}