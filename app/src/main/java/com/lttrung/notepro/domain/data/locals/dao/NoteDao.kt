package com.lttrung.notepro.domain.data.locals.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.lttrung.notepro.domain.data.locals.entities.NoteLocalsModel

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

    @Query("DELETE FROM Note")
    fun deleteAllNotes()

    @Query("SELECT * FROM Note WHERE id = :noteId LIMIT 1")
    fun getNoteDetails(noteId: String): NoteLocalsModel

    @Query("SELECT * FROM Note")
    fun getNotes(): List<NoteLocalsModel>
}