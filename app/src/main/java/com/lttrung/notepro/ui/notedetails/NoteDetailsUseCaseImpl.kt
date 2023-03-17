package com.lttrung.notepro.ui.notedetails

import com.lttrung.notepro.database.data.locals.entities.Note
import com.lttrung.notepro.database.repositories.MemberRepositories
import com.lttrung.notepro.database.repositories.NoteRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class NoteDetailsUseCaseImpl @Inject constructor(
    private val repositories: NoteRepositories,
    private val memberRepositories: MemberRepositories
) : NoteDetailsUseCase {
    override fun getNoteDetails(note: Note): Single<Note> {
        return repositories.getNoteDetails(note.id)
    }

    override fun updatePin(noteId: String, isPin: Boolean): Single<Boolean> {
        return memberRepositories.updatePin(noteId, isPin)
    }
}