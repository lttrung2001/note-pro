package com.lttrung.notepro.database.data.networks.impl

import com.lttrung.notepro.database.data.networks.NoteNetworks
import com.lttrung.notepro.database.data.networks.models.ApiResponse
import com.lttrung.notepro.database.data.networks.models.Note
import io.reactivex.rxjava3.core.Single
import retrofit2.Response
import retrofit2.http.*
import java.util.*

class NoteRetrofitServiceImp : NoteNetworks {
    interface Service {
        @POST("notes/add-note")
        fun addNote(@Body body: Map<String, Objects>): Single<Response<ApiResponse<Note>>>

        @PUT("notes/edit-note")
        fun editNote(
            @Query("id") id: String,
            @Body body: Map<String, Objects>
        ): Single<Response<ApiResponse<Note>>>

        @DELETE("notes/delete-note")
        fun deleteNote(@Query("id") id: String): Single<Response<ApiResponse<Unit>>>

        @GET("notes/get-note-details")
        fun getNoteDetails(@Query("id") id: String): Single<Response<ApiResponse<Note>>>

        @GET("notes/get-notes")
        fun getNotes(): Single<Response<ApiResponse<List<Note>>>>
    }

    override fun addNote(note: Note): Single<Note> {
        TODO("Not yet implemented")
    }

    override fun editNote(note: Note): Single<Note> {
        TODO("Not yet implemented")
    }

    override fun deleteNote(noteId: String): Single<Unit> {
        TODO("Not yet implemented")
    }

    override fun getNoteDetails(noteId: String): Single<Note> {
        TODO("Not yet implemented")
    }

    override fun getNotes(): Single<List<Note>> {
        TODO("Not yet implemented")
    }
}