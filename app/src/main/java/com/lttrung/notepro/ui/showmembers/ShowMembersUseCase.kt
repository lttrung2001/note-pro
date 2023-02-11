package com.lttrung.notepro.ui.showmembers

import com.lttrung.notepro.database.data.models.Member
import com.lttrung.notepro.database.data.models.Paging
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ShowMembersUseCase {
    fun getMembers(noteId: String, pageIndex: Int, limit: Int): Single<Paging<Member>>
}