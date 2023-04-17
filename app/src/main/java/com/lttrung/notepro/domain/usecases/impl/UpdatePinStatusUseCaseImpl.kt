package com.lttrung.notepro.domain.usecases.impl

import com.lttrung.notepro.domain.repositories.MemberRepositories
import com.lttrung.notepro.domain.usecases.UpdatePinStatusUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class UpdatePinStatusUseCaseImpl @Inject constructor(
    private val memberRepositories: MemberRepositories
) : UpdatePinStatusUseCase {
    override fun execute(noteId: String, isPin: Boolean): Single<Boolean> {
        return memberRepositories.updatePin(noteId, isPin)
    }
}