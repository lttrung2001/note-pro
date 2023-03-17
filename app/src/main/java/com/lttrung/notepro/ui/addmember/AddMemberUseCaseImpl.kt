package com.lttrung.notepro.ui.addmember

import com.lttrung.notepro.database.data.locals.entities.Member
import com.lttrung.notepro.database.repositories.MemberRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AddMemberUseCaseImpl @Inject constructor(
    private val repositories: MemberRepositories
): AddMemberUseCase {
    override fun addMember(noteId: String, email: String, role: String): Single<Member> {
        return repositories.addMember(noteId, email, role)
    }
}