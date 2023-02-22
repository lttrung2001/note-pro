package com.lttrung.notepro.database.data.networks.impl

import com.lttrung.notepro.database.data.networks.MemberNetworks
import com.lttrung.notepro.database.data.networks.models.ApiResponse
import com.lttrung.notepro.database.data.networks.models.Member
import com.lttrung.notepro.database.data.networks.models.Paging
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.*
import java.util.*
import javax.inject.Inject

class MemberRetrofitServiceImpl @Inject constructor(private val service: Service) : MemberNetworks {
    interface Service {

    }

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