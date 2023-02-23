package com.lttrung.notepro.di

import com.lttrung.notepro.database.data.locals.UserLocals
import com.lttrung.notepro.database.data.locals.impl.UserLocalsImpl
import com.lttrung.notepro.database.data.networks.UserNetworks
import com.lttrung.notepro.database.data.networks.impl.UserRetrofitServiceImpl
import com.lttrung.notepro.database.repositories.UserRepositories
import com.lttrung.notepro.database.repositories.impl.UserRepositoriesImpl
import com.lttrung.notepro.ui.login.LoginUseCase
import com.lttrung.notepro.ui.login.LoginUseCaseImpl
import com.lttrung.notepro.ui.register.RegisterUseCase
import com.lttrung.notepro.ui.register.RegisterUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindsModules {
    @Binds
    abstract fun bindsLoginUseCase(impl: LoginUseCaseImpl): LoginUseCase
    @Binds
    abstract fun bindsRegisterUseCase(impl: RegisterUseCaseImpl): RegisterUseCase
    @Binds
    abstract fun bindsUserRepositories(impl: UserRepositoriesImpl): UserRepositories
    @Binds
    abstract fun bindsUserNetworks(impl: UserRetrofitServiceImpl): UserNetworks
    @Binds
    abstract fun bindsUserLocals(impl: UserLocalsImpl): UserLocals
}