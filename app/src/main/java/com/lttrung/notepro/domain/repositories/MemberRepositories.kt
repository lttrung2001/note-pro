package com.lttrung.notepro.domain.repositories

import com.lttrung.notepro.domain.data.networks.MemberNetworks
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Paging
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface MemberRepositories {
    val networks: MemberNetworks
    fun addMember(noteId: String, email: String, role: String): Single<Member>
    fun editMember(noteId: String, member: Member): Single<Member>
    fun deleteMember(noteId: String, memberId: String): Single<Unit>
    fun getMemberDetails(noteId: String, memberId: String): Single<Member>
    fun getMembers(noteId: String, pageIndex: Int, limit: Int): Single<Paging<Member>>

    fun updatePin(noteId: String, isPin: Boolean): Single<Boolean>
}