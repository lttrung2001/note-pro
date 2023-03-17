package com.lttrung.notepro.ui.editmember

import com.lttrung.notepro.database.data.locals.entities.Member
import com.lttrung.notepro.database.repositories.MemberRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class EditMemberUseCaseImpl @Inject constructor(
    private val repositories: MemberRepositories
) : EditMemberUseCase {
    override fun editMember(noteId: String, member: Member): Single<Member> {
        return repositories.editMember(noteId, member)
    }

    override fun deleteMember(noteId: String, memberId: String): Single<Unit> {
        return repositories.deleteMember(noteId, memberId)
    }

    override fun getMemberDetails(noteId: String, memberId: String): Single<Member> {
        return repositories.getMemberDetails(noteId, memberId)
    }
}