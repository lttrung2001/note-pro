package com.lttrung.notepro.domain.repositories

import com.lttrung.notepro.domain.data.networks.MemberNetworks
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Paging
import javax.inject.Singleton

@Singleton
interface MemberRepositories {
    val networks: MemberNetworks
    suspend fun addMember(noteId: String, email: String, role: String): Member
    suspend fun editMember(noteId: String, member: Member): Member
    suspend fun deleteMember(noteId: String, memberId: String? = null)
    suspend fun getMemberDetails(noteId: String, memberId: String): Member
    suspend fun getMembers(noteId: String, pageIndex: Int, limit: Int): Paging<Member>

    suspend fun updatePin(noteId: String, isPin: Boolean): Boolean
}