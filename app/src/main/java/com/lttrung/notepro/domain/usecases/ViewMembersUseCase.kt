package com.lttrung.notepro.domain.usecases

import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Paging
import io.reactivex.rxjava3.core.Single
import javax.inject.Singleton

@Singleton
interface ViewMembersUseCase {
    fun execute(noteId: String, pageIndex: Int, limit: Int): Single<Paging<Member>>
}