package com.lttrung.notepro.domain.repositories.impl

import com.lttrung.notepro.domain.data.networks.MemberNetworks
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Paging
import com.lttrung.notepro.domain.repositories.MemberRepositories
import javax.inject.Inject

class MemberRepositoriesImpl @Inject constructor(override val networks: MemberNetworks) :
    MemberRepositories {
    override suspend fun addMember(noteId: String, email: String, role: String): Member {
        return networks.addMember(noteId, email, role).data
    }

    override suspend fun editMember(noteId: String, member: Member): Member {
        return networks.editMember(noteId, member).data
    }

    override suspend fun deleteMember(noteId: String, memberId: String) {
        return networks.deleteMember(noteId, memberId).data
    }

    override suspend fun getMemberDetails(noteId: String, memberId: String): Member {
        return networks.getMemberDetails(noteId, memberId).data
    }

    override suspend fun getMembers(noteId: String, pageIndex: Int, limit: Int): Paging<Member> {
        return networks.getMembers(noteId, pageIndex, limit).data
    }

    override suspend fun updatePin(noteId: String, isPin: Boolean): Boolean {
        return networks.updatePin(noteId, isPin).data
    }
}