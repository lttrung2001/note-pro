package com.lttrung.notepro.domain.data.networks.impl

import com.lttrung.notepro.domain.data.networks.MemberNetworks
import com.lttrung.notepro.domain.data.networks.models.ApiResponse
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Paging
import com.lttrung.notepro.utils.HttpStatusCodes
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.*
import java.util.*
import javax.inject.Inject

class MemberRetrofitServiceImpl @Inject constructor(private val service: Service) : MemberNetworks {
    interface Service {
        @FormUrlEncoded
        @POST("$PATH/add-member")
        fun addMember(
            @Query("noteId") noteId: String,
            @Field("email") email: String,
            @Field("role") role: String
        ): Single<Response<ApiResponse<Member>>>

        @FormUrlEncoded
        @PUT("$PATH/edit-member")
        fun editMember(
            @Query("noteId") noteId: String,
            @Query("memberId") memberId: String,
            @Field("role") role: String
        ): Single<Response<ApiResponse<Member>>>

        @DELETE("$PATH/delete-member")
        fun deleteMember(
            @Query("noteId") noteId: String,
            @Query("memberId") memberId: String
        ): Single<Response<ApiResponse<Unit>>>

        @GET("$PATH/get-member-details")
        fun getMemberDetails(
            @Query("noteId") noteId: String,
            @Query("memberId") memberId: String
        ): Single<Response<ApiResponse<Member>>>

        @GET("$PATH/get-members")
        fun getMembers(
            @Query("noteId") noteId: String,
            @Query("pageIndex") pageIndex: Int,
            @Query("limit") limit: Int
        ): Single<Response<ApiResponse<Paging<Member>>>>
        @FormUrlEncoded
        @PUT("$PATH/update-pin")
        fun updatePin(
            @Query("noteId") noteId: String,
            @Field("isPin") isPin: Boolean
        ): Single<Response<ApiResponse<Boolean>>>
    }

    override fun addMember(noteId: String, email: String, role: String): Single<Member> {
        return service.addMember(noteId, email, role).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.message())
            }
        }
    }

    override fun editMember(noteId: String, member: Member): Single<Member> {
        return service.editMember(noteId, member.id, member.role).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.message())
            }
        }
    }

    override fun deleteMember(noteId: String, memberId: String): Single<Unit> {
        return service.deleteMember(noteId, memberId).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.message())
            }
        }
    }

    override fun getMemberDetails(noteId: String, memberId: String): Single<Member> {
        return service.getMemberDetails(noteId, memberId).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                val data = response.body()!!.data
                Member(
                    data.id,
                    data.email,
                    data.fullName,
                    data.role,
                    data.phoneNumber.replace("+84", "0")
                )
            } else {
                throw Exception(response.message())
            }
        }
    }

    override fun getMembers(noteId: String, pageIndex: Int, limit: Int): Single<Paging<Member>> {
        return service.getMembers(noteId, pageIndex, limit).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.message())
            }
        }
    }

    override fun updatePin(noteId: String, isPin: Boolean): Single<Boolean> {
        return service.updatePin(noteId, isPin).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.message())
            }
        }
    }

    companion object {
        private const val PATH = "/api/v1/members"
    }
}