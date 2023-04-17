package com.lttrung.notepro.di

import com.lttrung.notepro.domain.data.locals.UserLocals
import com.lttrung.notepro.domain.data.locals.impl.UserLocalsImpl
import com.lttrung.notepro.domain.data.networks.LoginNetworks
import com.lttrung.notepro.domain.data.networks.MemberNetworks
import com.lttrung.notepro.domain.data.networks.NoteNetworks
import com.lttrung.notepro.domain.data.networks.UserNetworks
import com.lttrung.notepro.domain.data.networks.impl.LoginRetrofitServiceImpl
import com.lttrung.notepro.domain.data.networks.impl.MemberRetrofitServiceImpl
import com.lttrung.notepro.domain.data.networks.impl.NoteRetrofitServiceImpl
import com.lttrung.notepro.domain.data.networks.impl.UserRetrofitServiceImpl
import com.lttrung.notepro.domain.repositories.*
import com.lttrung.notepro.domain.repositories.impl.*
import com.lttrung.notepro.domain.usecases.*
import com.lttrung.notepro.domain.usecases.impl.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class AppBindsModules {
    @Binds
    abstract fun bindsLoginUseCase(impl: LoginUseCaseImpl): LoginUseCase

    @Binds
    abstract fun bindsRegisterUseCase(impl: RegisterUseCaseImpl): RegisterUseCase

    @Binds
    abstract fun bindsNoteDetailsUseCase(impl: NoteDetailsUseCaseImpl): NoteDetailsUseCase
    @Binds
    abstract fun bindsEditNoteUseCase(impl: EditNoteUseCaseImpl): EditNoteUseCase
    @Binds
    abstract fun bindsShowMembersUseCase(impl: ShowMembersUseCaseImpl): ShowMembersUseCase
    @Binds
    abstract fun bindsAddNoteUseCase(impl: AddNoteUseCaseImpl): AddNoteUseCase
    @Binds
    abstract fun bindsGetProfileUseCase(impl: ViewProfileUseCaseImpl): ViewProfileUseCase
    @Binds
    abstract fun bindsChangeProfileUseCase(impl: ChangeProfileUseCaseImpl): ChangeProfileUseCase
    @Binds
    abstract fun bindsChangePasswordUseCase(impl: ChangePasswordUseCaseImpl): ChangePasswordUseCase

    @Binds
    abstract fun bindsEditMemberUseCase(impl: EditMemberUseCaseImpl): EditMemberUseCase

    @Binds
    abstract fun bindsForgotPasswordUseCase(impl: ForgotPasswordUseCaseImpl): ForgotPasswordUseCase

    @Binds
    abstract fun bindsResetPasswordUseCase(impl: ResetPasswordUseCaseImpl): ResetPasswordUseCase

    @Binds
    abstract fun bindsAddMemberUseCase(impl: AddMemberUseCaseImpl): AddMemberUseCase

    @Binds
    abstract fun bindsChatUseCase(impl: ChatUseCaseImpl): ChatUseCase

    @Binds
    abstract fun bindsDeleteMemberUseCase(impl: DeleteMemberUseCaseImpl): DeleteMemberUseCase

    @Binds
    abstract fun bindsDeleteNoteUseCase(impl: DeleteNoteUseCaseImpl): DeleteNoteUseCase

    @Binds
    abstract fun bindsGetCurrentUserUseCase(impl: GetCurrentUserUseCaseImpl): GetCurrentUserUseCase

    @Binds
    abstract fun bindsGetMemberDetailsUseCase(impl: GetMemberDetailsUseCaseImpl): GetMemberDetailsUseCase

    @Binds
    abstract fun bindsGetNoteDetailsUseCase(impl: GetNoteDetailsUseCaseImpl): GetNoteDetailsUseCase

    @Binds
    abstract fun bindsGetNotesUseCase(impl: GetNotesUseCaseImpl): GetNotesUseCase

    @Binds
    abstract fun bindsLogoutUseCase(impl: LogoutUseCaseImpl): LogoutUseCase

    @Binds
    abstract fun bindsUpdatePinStatusUseCase(impl: UpdatePinStatusUseCaseImpl): UpdatePinStatusUseCase

    @Binds
    abstract fun bindsLoginRepositories(impl: LoginRepositoriesImpl): LoginRepositories

    @Binds
    abstract fun bindsNoteRepositories(impl: NoteRepositoriesImpl): NoteRepositories

    @Binds
    abstract fun bindsMemberRepositories(impl: MemberRepositoriesImpl): MemberRepositories

    @Binds
    abstract fun bindsUserRepositories(impl: UserRepositoriesImpl): UserRepositories

    @Binds
    abstract fun bindsMessageRepositories(impl: MessageRepositoriesImpl): MessageRepositories


    @Binds
    abstract fun bindsLoginNetworks(impl: LoginRetrofitServiceImpl): LoginNetworks
    @Binds
    abstract fun bindsNoteNetworks(impl: NoteRetrofitServiceImpl): NoteNetworks
    @Binds
    abstract fun bindsMemberNetworks(impl: MemberRetrofitServiceImpl): MemberNetworks
    @Binds
    abstract fun bindsUserNetworks(impl: UserRetrofitServiceImpl): UserNetworks


    @Binds
    abstract fun bindsUserLocals(impl: UserLocalsImpl): UserLocals
}