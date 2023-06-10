package com.lttrung.notepro.domain.data.networks

import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Paging
import javax.inject.Singleton

@Singleton
interface MemberNetworks {
    suspend fun addMember(noteId: String, email: String, role: String): ResponseEntity<Member>
    suspend fun editMember(noteId: String, member: Member): ResponseEntity<Member>
    suspend fun deleteMember(noteId: String, memberId: String): ResponseEntity<Unit>
    suspend fun getMemberDetails(noteId: String, memberId: String): ResponseEntity<Member>
    suspend fun getMembers(noteId: String, pageIndex: Int, limit: Int): ResponseEntity<Paging<Member>>

    suspend fun updatePin(noteId: String, isPin: Boolean): ResponseEntity<Boolean>
}