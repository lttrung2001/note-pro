package com.lttrung.notepro.domain.repositories.impl

import com.lttrung.notepro.domain.data.networks.MemberNetworks
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Paging
import com.lttrung.notepro.domain.repositories.MemberRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class MemberRepositoriesImpl @Inject constructor(override val networks: MemberNetworks) :
    MemberRepositories {
    override fun addMember(noteId: String, email: String, role: String): Single<Member> {
        return networks.addMember(noteId, email, role)
    }

    override fun editMember(noteId: String, member: Member): Single<Member> {
        return networks.editMember(noteId, member)
    }

    override fun deleteMember(noteId: String, memberId: String): Single<Unit> {
        return networks.deleteMember(noteId, memberId)
    }

    override fun getMemberDetails(noteId: String, memberId: String): Single<Member> {
        return networks.getMemberDetails(noteId, memberId)
    }

    override fun getMembers(noteId: String, pageIndex: Int, limit: Int): Single<Paging<Member>> {
        return networks.getMembers(noteId, pageIndex, limit)
    }

    override fun updatePin(noteId: String, isPin: Boolean): Single<Boolean> {
        return networks.updatePin(noteId, isPin)
    }
}