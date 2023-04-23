package com.lttrung.notepro.domain.usecases

import com.lttrung.notepro.domain.data.locals.entities.CurrentUser
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface GetCurrentUserUseCase {
    fun execute(): Single<CurrentUser>
}