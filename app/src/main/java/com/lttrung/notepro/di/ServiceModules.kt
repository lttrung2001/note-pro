package com.lttrung.notepro.di

import com.lttrung.notepro.domain.data.networks.impl.LoginRetrofitServiceImpl
import com.lttrung.notepro.domain.data.networks.impl.MemberRetrofitServiceImpl
import com.lttrung.notepro.domain.data.networks.impl.NoteRetrofitServiceImpl
import com.lttrung.notepro.domain.data.networks.impl.UserRetrofitServiceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServiceModules {
    @Provides
    @Singleton
    fun providesLoginService(@Named("NoTokenRetrofit") retrofit: Retrofit): LoginRetrofitServiceImpl.Service =
        retrofit.create(LoginRetrofitServiceImpl.Service::class.java)

    @Provides
    @Singleton
    fun providesNoteService(@Named("TokenRetrofit") retrofit: Retrofit): NoteRetrofitServiceImpl.Service =
        retrofit.create(NoteRetrofitServiceImpl.Service::class.java)

    @Provides
    @Singleton
    fun providesMemberService(@Named("TokenRetrofit") retrofit: Retrofit): MemberRetrofitServiceImpl.Service =
        retrofit.create(MemberRetrofitServiceImpl.Service::class.java)

    @Provides
    @Singleton
    fun providesUserService(@Named("TokenRetrofit") retrofit: Retrofit): UserRetrofitServiceImpl.Service =
        retrofit.create(UserRetrofitServiceImpl.Service::class.java)
}