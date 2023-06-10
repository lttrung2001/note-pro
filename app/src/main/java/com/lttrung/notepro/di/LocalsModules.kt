package com.lttrung.notepro.di

import com.lttrung.notepro.domain.data.locals.NoteLocals
import com.lttrung.notepro.domain.data.locals.UserLocals
import com.lttrung.notepro.domain.data.locals.impl.NoteLocalsImpl
import com.lttrung.notepro.domain.data.locals.impl.UserLocalsImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface LocalsModules {
    @Binds
    fun bindsUserLocals(impl: UserLocalsImpl): UserLocals
    @Binds
    fun bindsNoteLocals(impl: NoteLocalsImpl): NoteLocals
}