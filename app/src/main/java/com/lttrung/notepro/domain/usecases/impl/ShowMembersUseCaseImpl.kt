package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.data.networks.models.Member
import com.lttrung.notepro.domain.data.networks.models.Paging
import com.lttrung.notepro.domain.repositories.MemberRepositories
import com.lttrung.notepro.domain.usecases.ShowMembersUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ShowMembersUseCaseImpl @Inject constructor(
    private val repositories: MemberRepositories
) : ShowMembersUseCase {

    override fun execute(noteId: String, pageIndex: Int, limit: Int): Single<Paging<Member>> {
        return repositories.getMembers(noteId, pageIndex, limit)
    }
}