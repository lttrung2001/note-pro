package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.repositories.MemberRepositories
import com.lttrung.notepro.domain.usecases.GetMemberDetailsUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class GetMemberDetailsUseCaseImpl @Inject constructor(
    private val memberRepositories: MemberRepositories
) : GetMemberDetailsUseCase {
    override fun execute(noteId: String, memberId: String): Single<Member> {
        return memberRepositories.getMemberDetails(noteId, memberId)
    }
}