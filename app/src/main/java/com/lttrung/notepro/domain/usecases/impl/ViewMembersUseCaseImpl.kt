package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Paging
import com.lttrung.notepro.domain.repositories.MemberRepositories
import com.lttrung.notepro.domain.usecases.ViewMembersUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ViewMembersUseCaseImpl @Inject constructor(
    private val repositories: MemberRepositories
) : ViewMembersUseCase {

    override fun execute(noteId: String, pageIndex: Int, limit: Int): Single<Paging<Member>> {
        return repositories.getMembers(noteId, pageIndex, limit)
    }
}