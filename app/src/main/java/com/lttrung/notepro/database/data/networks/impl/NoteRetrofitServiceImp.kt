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