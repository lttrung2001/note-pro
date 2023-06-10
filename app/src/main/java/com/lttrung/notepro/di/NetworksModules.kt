package com.lttrung.notepro.di

import com.lttrung.notepro.domain.data.networks.LoginNetworks
import com.lttrung.notepro.domain.data.networks.MemberNetworks
import com.lttrung.notepro.domain.data.networks.NoteNetworks
import com.lttrung.notepro.domain.data.networks.UserNetworks
import com.lttrung.notepro.domain.data.networks.impl.LoginRetrofitServiceImpl
import com.lttrung.notepro.domain.data.networks.impl.MemberRetrofitServiceImpl
import com.lttrung.notepro.domain.data.networks.impl.NoteRetrofitServiceImpl
import com.lttrung.notepro.domain.data.networks.impl.UserRetrofitServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NetworksModules {
    @Binds
    fun bindsLoginNetworks(impl: LoginRetrofitServiceImpl): LoginNetworks

    @Binds
    fun bindsNoteNetworks(impl: NoteRetrofitServiceImpl): NoteNetworks

    @Binds
    fun bindsMemberNetworks(impl: MemberRetrofitServiceImpl): MemberNetworks

    @Binds
    fun bindsUserNetworks(impl: UserRetrofitServiceImpl): UserNetworks
}