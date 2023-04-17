package com.lttrung.notepro.domain.usecases

import com.lttrung.notepro.domain.data.networks.models.Member
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface GetMemberDetailsUseCase {
    fun execute(noteId: String, memberId: String): Single<Member>
}