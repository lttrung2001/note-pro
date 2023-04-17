package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.repositories.MemberRepositories
import com.lttrung.notepro.domain.usecases.AddMemberUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AddMemberUseCaseImpl @Inject constructor(
    private val repositories: MemberRepositories
): AddMemberUseCase {
    override fun execute(noteId: String, email: String, role: String): Single<Member> {
        return repositories.addMember(noteId, email, role)
    }
}