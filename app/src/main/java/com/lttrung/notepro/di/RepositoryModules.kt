package com.lttrung.notepro.di

import com.lttrung.notepro.domain.repositories.LoginRepositories
import com.lttrung.notepro.domain.repositories.MemberRepositories
import com.lttrung.notepro.domain.repositories.MessageRepositories
import com.lttrung.notepro.domain.repositories.NoteRepositories
import com.lttrung.notepro.domain.repositories.UserRepositories
import com.lttrung.notepro.domain.repositories.impl.LoginRepositoriesImpl
import com.lttrung.notepro.domain.repositories.impl.MemberRepositoriesImpl
import com.lttrung.notepro.domain.repositories.impl.MessageRepositoriesImpl
import com.lttrung.notepro.domain.repositories.impl.NoteRepositoriesImpl
import com.lttrung.notepro.domain.repositories.impl.UserRepositoriesImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModules {
    @Binds
    fun bindsLoginRepositories(impl: LoginRepositoriesImpl): LoginRepositories

    @Binds
    fun bindsNoteRepositories(impl: NoteRepositoriesImpl): NoteRepositories

    @Binds
    fun bindsMemberRepositories(impl: MemberRepositoriesImpl): MemberRepositories

    @Binds
    fun bindsUserRepositories(impl: UserRepositoriesImpl): UserRepositories

    @Binds
    fun bindsMessageRepositories(impl: MessageRepositoriesImpl): MessageRepositories
}