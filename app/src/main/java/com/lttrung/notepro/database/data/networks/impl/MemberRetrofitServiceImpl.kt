package com.lttrung.notepro.database.data.networks.impl

import com.lttrung.notepro.database.data.networks.MemberNetworks
import com.lttrung.notepro.database.data.networks.models.ApiResponse
import com.lttrung.notepro.database.data.networks.models.Member
import com.lttrung.notepro.database.data.networks.models.Paging
import com.lttrung.notepro.utils.HttpStatusCodes
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.*
import java.util.*
import javax.inject.Inject

class MemberRetrofitServiceImpl @Inject constructor(private val service: Service) : MemberNetworks {
    interface Service {
        @GET("$PATH/get-members")
        fun getMembers(
            @Query("noteId") noteId: String,
            @Query("pageIndex") pageIndex: Int,
            @Query("limit") limit: Int
        ): Single<Response<ApiResponse<Paging<Member>>>>
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

    override fun getMembers(noteId: String, pageIndex: Int, limit: Int): Single<Paging<Member>> {
        return service.getMembers(noteId, pageIndex, limit).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.body()!!.message)
            }
        }
    }

    companion object {
        private const val PATH = "/api/v1/members"
    }
}