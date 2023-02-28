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
        @GET("$PATH/get-notes")
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
        return service.getNotes().map { response ->
            if (response.code() == HttpStatusCodes.OK.code) {
                response.body()!!.data
            } else {
                throw Exception(response.body()!!.message)
            }
        }
    }
}