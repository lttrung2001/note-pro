package com.lttrung.notepro.database.data.networks.impl

import android.webkit.URLUtil
import com.lttrung.notepro.database.data.networks.NoteNetworks
import com.lttrung.notepro.database.data.networks.models.ApiResponse
import com.lttrung.notepro.database.data.networks.models.Note
import com.lttrung.notepro.utils.HttpStatusCodes
import io.reactivex.rxjava3.core.Single
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
    private val service: Service
) : NoteNetworks {
    companion object {
        private const val PATH = "/api/v1/notes"
    }

    interface Service {
        @Multipart
        @POST("$PATH/add-note")
        fun addNote(
            @Part("title") title: RequestBody,
            @Part("content") content: RequestBody,
            @Part("isPin") isPin: Boolean,
            @Part images: List<MultipartBody.Part>?
        ): Single<Response<ApiResponse<Note>>>

        @Multipart
        @PUT("$PATH/edit-note")
        fun editNote(
            @Query("id") id: String,
            @Part("title") title: RequestBody,
            @Part("content") content: RequestBody,
            @Part("isPin") isPin: Boolean,
            @Part("deleteImageIds") ids: List<String>,
            @Part images: List<MultipartBody.Part>?
        ): Single<Response<ApiResponse<Note>>>

        @DELETE("$PATH/delete-note")
        fun deleteNote(@Query("id") noteId: String): Single<Response<ApiResponse<Unit>>>

        @GET("$PATH/get-notes")
        fun getNotes(): Single<Response<ApiResponse<List<Note>>>>

        @GET("$PATH/get-note-details")
        fun getNoteDetails(@Query("id") noteId: String): Single<Response<ApiResponse<Note>>>
    }

    override fun addNote(note: Note): Single<Note> {
        val title = note.title.toRequestBody(MultipartBody.FORM)
        val content = note.content.toRequestBody(MultipartBody.FORM)
        val parts = note.images?.map { image ->
            val file = File(image.url)
            val requestBody = file.asRequestBody(MultipartBody.FORM)
            MultipartBody.Part.createFormData("images", file.name, requestBody)
        }
        return service.addNote(title, content, note.isPin, parts).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.body()!!.message)
            }
        }
    }

    override fun editNote(note: Note, deleteImageIds: List<String>): Single<Note> {
        val newImages = note.images?.filter { image ->
            !URLUtil.isNetworkUrl(image.url)
        }?.map { image ->
            val file = File(image.url)
            val requestBody = file.asRequestBody(MultipartBody.FORM)
            MultipartBody.Part.createFormData("images", file.name, requestBody)
        }
        return service.editNote(
            note.id,
            note.title.toRequestBody(MultipartBody.FORM),
            note.content.toRequestBody(MultipartBody.FORM),
            note.isPin,
            deleteImageIds,
            newImages
        ).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.body()!!.message)
            }
        }
    }

    override fun deleteNote(noteId: String): Single<Unit> {
        return service.deleteNote(noteId).map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.body()!!.message)
            }
        }
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