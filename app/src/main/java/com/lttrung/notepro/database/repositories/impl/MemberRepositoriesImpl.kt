package com.lttrung.notepro.database.repositories.impl

import com.lttrung.notepro.database.data.networks.models.Member
import com.lttrung.notepro.database.data.networks.models.Paging
import com.lttrung.notepro.database.repositories.MemberRepositories
import io.reactivex.rxjava3.core.Single

class MemberRepositoriesImpl : MemberRepositories {
    override fun addMember(noteId: String, email: String): Single<Member> {
        TODO("Not yet implemented")
    }

    override fun editMember(noteId: String, member: Member): Single<Member> {
        TODO("Not yet implemented")
    }

    override fun deleteMember(noteId: String, memberId: String): Single<Unit> {
        TODO("Not yet implemented")
    }

    override fun getMemberDetails(noteId: String, memberId: String): Single<Member> {
        TODO("Not yet implemented")
    }

    override fun getMembers(noteId: String): Single<Paging<Member>> {
        TODO("Not yet implemented")
    }
}