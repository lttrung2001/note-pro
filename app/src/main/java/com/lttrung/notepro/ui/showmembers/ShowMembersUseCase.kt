package com.lttrung.notepro.ui.showmembers

import com.lttrung.notepro.database.data.networks.models.Member
import com.lttrung.notepro.database.data.networks.models.Paging
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ShowMembersUseCase {
    fun getMembers(noteId: String, pageIndex: Int, limit: Int): Single<Paging<Member>>
}