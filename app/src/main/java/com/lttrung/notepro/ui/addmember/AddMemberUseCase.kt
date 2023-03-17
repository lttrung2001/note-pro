package com.lttrung.notepro.ui.addmember

import com.lttrung.notepro.database.data.locals.entities.Member
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface AddMemberUseCase {
  fun addMember(noteId: String, email: String, role: String): Single<Member>
}