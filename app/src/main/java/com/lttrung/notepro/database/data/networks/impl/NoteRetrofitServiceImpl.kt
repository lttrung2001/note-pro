package com.lttrung.notepro.database.data.networks.impl

import com.lttrung.notepro.database.data.networks.NoteNetworks
import com.lttrung.notepro.database.data.networks.models.ApiResponse
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.utils.HttpStatusCodes
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.*
import java.util.*
import javax.inject.Inject

class NoteRetrofitServiceImpl @Inject constructor(
    private val service: Service
) : NoteNetworks {
    companion object {
        private const val PATH = "/api/v1/notes"
    }

    interface Service {
        @FormUrlEncoded
        @PUT("$PATH/edit-note")
        fun editNote(
            @Query("id") id: String,
            @Field("title") title: String,
            @Field("content") content: String,
            @Field("isPin") isPin: Boolean,
            @Field("deleteImageIds") ids: List<String>
        ): Single<Response<ApiResponse<Note>>>

        @GET("$PATH/get-notes")
        fun getNotes(): Single<Response<ApiResponse<List<Note>>>>

        @GET("$PATH/get-note-details")
        fun getNoteDetails(@Query("id") noteId: String): Single<Response<ApiResponse<Note>>>
    }

    override fun addNote(note: Note): Single<Note> {
        TODO("Not yet implemented")
    }

    override fun editNote(note: Note, deleteImageIds: List<String>): Single<Note> {
        return service.editNote(
            note.id,
            note.title,
            note.content,
            note.isPin,
            deleteImageIds
        ).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.body()!!.message)
            }
        }
    }

    override fun deleteNote(noteId: String): Single<Unit> {
        TODO("Not yet implemented")
    }

    override fun getNoteDetails(noteId: String): Single<Note> {
        return service.getNoteDetails(noteId).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.body()!!.message)
            }
        }
    }

    override fun getNotes(): Single<List<Note>> {
        return service.getNotes().map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.body()!!.message)
            }
        }
    }
}