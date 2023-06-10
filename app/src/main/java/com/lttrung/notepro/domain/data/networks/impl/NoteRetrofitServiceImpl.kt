package com.lttrung.notepro.domain.data.networks.impl

import android.webkit.URLUtil
import com.google.gson.Gson
import com.lttrung.notepro.domain.data.networks.NoteNetworks
import com.lttrung.notepro.domain.data.networks.ResponseEntity
import com.lttrung.notepro.domain.data.networks.models.Note
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.http.*
import java.io.File
import java.util.*
import javax.inject.Inject

class NoteRetrofitServiceImpl @Inject constructor(
    private val service: Service,
    private val gson: Gson
) : NoteNetworks {
    companion object {
        private const val PATH = "/api/v1/notes"
    }

    interface Service {
        @Multipart
        @POST("$PATH/add-note")
        suspend fun addNote(
            @Part("title") title: RequestBody,
            @Part("content") content: RequestBody,
            @Part("isPin") isPin: Boolean,
            @Part images: List<MultipartBody.Part>?
        ): Response<ResponseEntity<Note>>

        @Multipart
        @PUT("$PATH/edit-note")
        suspend fun editNote(
            @Query("id") id: String,
            @Part("title") title: RequestBody,
            @Part("content") content: RequestBody,
            @Part("isPin") isPin: Boolean,
            @Part("isArchived") isArchived: Boolean,
            @Part("isRemoved") isRemoved: Boolean,
            @Part("deleteImageIds") ids: RequestBody,
            @Part images: List<MultipartBody.Part>?
        ): Response<ResponseEntity<Note>>

        @DELETE("$PATH/delete-note")
        suspend fun deleteNote(@Query("id") noteId: String): Response<ResponseEntity<Unit>>

        @GET("$PATH/get-notes")
        suspend fun getNotes(): Response<ResponseEntity<List<Note>>>

        @GET("$PATH/get-note-details")
        suspend fun getNoteDetails(@Query("id") noteId: String): Response<ResponseEntity<Note>>
    }

    override suspend fun addNote(note: Note): ResponseEntity<Note> {
        val title = note.title.toRequestBody(MultipartBody.FORM)
        val content = note.content.toRequestBody(MultipartBody.FORM)
        val parts = note.images.map { image ->
            val file = File(image.url)
            val requestBody = file.asRequestBody(MultipartBody.FORM)
            MultipartBody.Part.createFormData("images", file.name, requestBody)
        }
        val response = service.addNote(title, content, note.isPin, parts)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception(body?.message)
        }
    }

    override suspend fun editNote(note: Note, deleteImageIds: List<String>): ResponseEntity<Note> {
        val newImages = note.images.filter { image ->
            !URLUtil.isNetworkUrl(image.url)
        }.map { image ->
            val file = File(image.url)
            val requestBody = file.asRequestBody(MultipartBody.FORM)
            MultipartBody.Part.createFormData("images", file.name, requestBody)
        }
        val response = service.editNote(
            note.id,
            note.title.toRequestBody(MultipartBody.FORM),
            note.content.toRequestBody(MultipartBody.FORM),
            note.isPin, note.isArchived, note.isRemoved,
            gson.toJson(deleteImageIds).toRequestBody(MultipartBody.FORM),
            newImages
        )
        val body = response.body()

        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception(body?.message)
        }
    }

    override suspend fun deleteNote(noteId: String): ResponseEntity<Unit> {
        val response = service.deleteNote(noteId)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception(body?.message)
        }
    }

    override suspend fun getNoteDetails(noteId: String): ResponseEntity<Note> {
        val response = service.getNoteDetails(noteId)
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception(body?.message)
        }
    }

    override suspend fun getNotes(): ResponseEntity<List<Note>> {
        val response = service.getNotes()
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            body
        } else {
            throw Exception(body?.message)
        }
    }
}