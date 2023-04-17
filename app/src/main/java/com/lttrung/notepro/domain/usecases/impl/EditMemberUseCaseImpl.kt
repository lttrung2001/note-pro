package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.repositories.MemberRepositories
import com.lttrung.notepro.domain.usecases.EditMemberUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class EditMemberUseCaseImpl @Inject constructor(
    private val repositories: MemberRepositories
) : EditMemberUseCase {
    override fun execute(noteId: String, member: Member): Single<Member> {
        return repositories.editMember(noteId, member)
    }
}