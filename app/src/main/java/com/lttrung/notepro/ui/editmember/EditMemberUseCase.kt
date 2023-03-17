package com.lttrung.notepro.ui.editmember

import com.lttrung.notepro.database.data.locals.entities.Member
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface EditMemberUseCase {
    fun editMember(noteId: String, member: Member): Single<Member>
    fun deleteMember(noteId: String, memberId: String): Single<Unit>
    fun getMemberDetails(noteId: String, memberId: String): Single<Member>
}