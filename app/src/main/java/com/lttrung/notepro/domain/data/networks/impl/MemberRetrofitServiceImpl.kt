package com.lttrung.notepro.domain.data.networks.impl

import com.lttrung.notepro.domain.data.networks.MemberNetworks
import com.lttrung.notepro.domain.data.networks.ResponseEntity
import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Paging
import retrofit2.Response
import retrofit2.http.*
import java.util.*
import javax.inject.Inject

class MemberRetrofitServiceImpl @Inject constructor(private val service: Service) : MemberNetworks {
    companion object {
        private const val PATH = "/api/v1/members"
    }

    interface Service {
        @FormUrlEncoded
        @POST("$PATH/add-member")
        suspend fun addMember(
            @Query("noteId") noteId: String,
            @Field("email") email: String,
            @Field("role") role: String
        ): Response<ResponseEntity<Member>>

        @FormUrlEncoded
        @PUT("$PATH/edit-member")
        suspend fun editMember(
            @Query("noteId") noteId: String,
            @Query("memberId") memberId: String,
            @Field("role") role: String
        ): Response<ResponseEntity<Member>>

        @DELETE("$PATH/delete-member")
        suspend fun deleteMember(
            @Query("noteId") noteId: String,
            @Query("memberId") memberId: String?
        ): Response<ResponseEntity<Unit>>

        @GET("$PATH/get-member-details")
        suspend fun getMemberDetails(
            @Query("noteId") noteId: String,
            @Query("memberId") memberId: String
        ): Response<ResponseEntity<Member>>

        @GET("$PATH/get-members")
        suspend fun getMembers(
            @Query("noteId") noteId: String,
            @Query("pageIndex") pageIndex: Int,
            @Query("limit") limit: Int
        ): Response<ResponseEntity<Paging<Member>>>

        @FormUrlEncoded
        @PUT("$PATH/update-pin")
        suspend fun updatePin(
            @Query("noteId") noteId: String,
            @Field("isPin") isPin: Boolean
        ): Response<ResponseEntity<Boolean>>
    }

    override suspend fun addMember(
        noteId: String,
        email: String,
        role: String
    ): ResponseEntity<Member> {
        val response = service.addMember(noteId, email, role)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception("Đã có lỗi xảy ra. Vui lòng thử lại sau.")
        }
    }

    override suspend fun editMember(noteId: String, member: Member): ResponseEntity<Member> {
        val response = service.editMember(noteId, member.id, member.role)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception("Đã có lỗi xảy ra. Vui lòng thử lại sau.")
        }
    }

    override suspend fun deleteMember(noteId: String, memberId: String?): ResponseEntity<Unit> {
        val response = service.deleteMember(noteId, memberId)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception("Đã có lỗi xảy ra. Vui lòng thử lại sau.")
        }
    }

    override suspend fun getMemberDetails(
        noteId: String,
        memberId: String
    ): ResponseEntity<Member> {
        val response = service.getMemberDetails(noteId, memberId)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception("Đã có lỗi xảy ra. Vui lòng thử lại sau.")
        }
    }

    override suspend fun getMembers(
        noteId: String,
        pageIndex: Int,
        limit: Int
    ): ResponseEntity<Paging<Member>> {
        val response = service.getMembers(noteId, pageIndex, limit)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception("Đã có lỗi xảy ra. Vui lòng thử lại sau.")
        }
    }

    override suspend fun updatePin(noteId: String, isPin: Boolean): ResponseEntity<Boolean> {
        val response = service.updatePin(noteId, isPin)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception("Đã có lỗi xảy ra. Vui lòng thử lại sau.")
        }
    }
}