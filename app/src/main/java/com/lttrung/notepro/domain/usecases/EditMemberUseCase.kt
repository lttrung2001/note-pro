package com.lttrung.notepro.domain.usecases

import com.lttrung.notepro.domain.data.networks.models.Member
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface EditMemberUseCase {
    fun execute(noteId: String, member: Member): Single<Member>
}