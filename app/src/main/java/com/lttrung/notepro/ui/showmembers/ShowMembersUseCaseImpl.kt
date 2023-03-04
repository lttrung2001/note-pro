package com.lttrung.notepro.ui.showmembers

import com.lttrung.notepro.database.data.networks.models.Member
import com.lttrung.notepro.database.data.networks.models.Paging
import com.lttrung.notepro.database.repositories.MemberRepositories
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class ShowMembersUseCaseImpl @Inject constructor(
    private val repositories: MemberRepositories
) : ShowMembersUseCase {
    override fun getMembers(noteId: String, pageIndex: Int, limit: Int): Single<Paging<Member>> {
        return repositories.getMembers(noteId, pageIndex, limit)
    }
}