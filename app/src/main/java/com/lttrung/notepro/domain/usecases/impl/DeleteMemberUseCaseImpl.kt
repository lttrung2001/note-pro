package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.repositories.MemberRepositories
import com.lttrung.notepro.domain.usecases.DeleteMemberUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class DeleteMemberUseCaseImpl @Inject constructor(
    private val memberRepositories: MemberRepositories
) : DeleteMemberUseCase {
    override fun execute(noteId: String, memberId: String): Single<Unit> {
        return memberRepositories.deleteMember(noteId, memberId)
    }
}