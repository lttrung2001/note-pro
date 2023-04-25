package com.lttrung.notepro.domain.repositories.impl

import com.lttrung.notepro.domain.data.locals.UserLocals
import com.lttrung.notepro.domain.data.locals.room.entities.CurrentUser
import com.lttrung.notepro.domain.data.networks.UserNetworks
import com.lttrung.notepro.domain.data.networks.models.UserInfo
import com.lttrung.notepro.domain.repositories.UserRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class UserRepositoriesImpl @Inject constructor(
    override val networks: UserNetworks,
    override val locals: UserLocals
) : UserRepositories {
    override fun changePassword(oldPassword: String, newPassword: String): Single<String> {
        return networks.changePassword(oldPassword, newPassword).doAfterSuccess { refreshToken ->
            locals.changePassword(newPassword, refreshToken)
        }
    }

    override fun changeProfile(fullName: String, phoneNumber: String): Single<UserInfo> {
        return networks.changeProfile(fullName, phoneNumber).doAfterSuccess {
            locals.changeProfile(fullName, phoneNumber)
        }
    }

    override fun getProfile(): Single<UserInfo> {
        return networks.getProfile().doAfterSuccess { user ->
            locals.changeProfile(user.fullName, user.phoneNumber)
        }.onErrorReturn {
            locals.getCurrentUser().map {
                UserInfo(it.id!!, it.email, it.fullName!!, it.phoneNumber!!)
            }.blockingGet()
        }
    }

    override fun getCurrentUser(): Single<CurrentUser> {
        return locals.getCurrentUser()
    }

    override fun logout() {
        return locals.logout()
    }
}