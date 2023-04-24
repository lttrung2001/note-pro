package com.lttrung.notepro.domain.data.locals.room

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.lttrung.notepro.domain.data.locals.entities.NoteLocalsModel
import io.reactivex.rxjava3.core.Single

@Dao
interface NoteDao {
    @Insert(onConflict = REPLACE)
    fun addNote(note: NoteLocalsModel)

    @Insert(onConflict = REPLACE)
    fun addNotes(notes: List<NoteLocalsModel>)

    @Update(onConflict = REPLACE)
    fun editNote(note: NoteLocalsModel)

    @Delete
    fun deleteNote(note: NoteLocalsModel)

    @Query("SELECT * FROM Note WHERE id = :noteId LIMIT 1")
    fun getNoteDetails(noteId: String): Single<NoteLocalsModel>

    @Query("SELECT * FROM Note")
    fun getNotes(): Single<List<NoteLocalsModel>>
}