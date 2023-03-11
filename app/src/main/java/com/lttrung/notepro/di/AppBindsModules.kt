package com.lttrung.notepro.di

import com.lttrung.notepro.database.data.locals.UserLocals
import com.lttrung.notepro.database.data.locals.impl.UserLocalsImpl
import com.lttrung.notepro.database.data.networks.LoginNetworks
import com.lttrung.notepro.database.data.networks.MemberNetworks
import com.lttrung.notepro.database.data.networks.NoteNetworks
import com.lttrung.notepro.database.data.networks.UserNetworks
import com.lttrung.notepro.database.data.networks.impl.LoginRetrofitServiceImpl
import com.lttrung.notepro.database.data.networks.impl.MemberRetrofitServiceImpl
import com.lttrung.notepro.database.data.networks.impl.NoteRetrofitServiceImpl
import com.lttrung.notepro.database.data.networks.impl.UserRetrofitServiceImpl
import com.lttrung.notepro.database.repositories.LoginRepositories
import com.lttrung.notepro.database.repositories.MemberRepositories
import com.lttrung.notepro.database.repositories.NoteRepositories
import com.lttrung.notepro.database.repositories.UserRepositories
import com.lttrung.notepro.database.repositories.impl.LoginRepositoriesImpl
import com.lttrung.notepro.database.repositories.impl.MemberRepositoriesImpl
import com.lttrung.notepro.database.repositories.impl.NoteRepositoriesImpl
import com.lttrung.notepro.database.repositories.impl.UserRepositoriesImpl
import com.lttrung.notepro.ui.addnote.AddNoteUseCase
import com.lttrung.notepro.ui.addnote.AddNoteUseCaseImpl
import com.lttrung.notepro.ui.changepassword.ChangePasswordUseCase
import com.lttrung.notepro.ui.changepassword.ChangePasswordUseCaseImpl
import com.lttrung.notepro.ui.changeprofile.ChangeProfileUseCase
import com.lttrung.notepro.ui.changeprofile.ChangeProfileUseCaseImpl
import com.lttrung.notepro.ui.editmember.EditMemberUseCase
import com.lttrung.notepro.ui.editmember.EditMemberUseCaseImpl
import com.lttrung.notepro.ui.editnote.EditNoteUseCase
import com.lttrung.notepro.ui.editnote.EditNoteUseCaseImpl
import com.lttrung.notepro.ui.forgotpassword.ForgotPasswordUseCase
import com.lttrung.notepro.ui.forgotpassword.ForgotPasswordUseCaseImpl
import com.lttrung.notepro.ui.login.LoginUseCase
import com.lttrung.notepro.ui.login.LoginUseCaseImpl
import com.lttrung.notepro.ui.main.MainUseCase
import com.lttrung.notepro.ui.main.MainUseCaseImpl
import com.lttrung.notepro.ui.notedetails.NoteDetailsUseCase
import com.lttrung.notepro.ui.notedetails.NoteDetailsUseCaseImpl
import com.lttrung.notepro.ui.register.RegisterUseCase
import com.lttrung.notepro.ui.register.RegisterUseCaseImpl
import com.lttrung.notepro.ui.resetpassword.ResetPasswordUseCase
import com.lttrung.notepro.ui.resetpassword.ResetPasswordUseCaseImpl
import com.lttrung.notepro.ui.showmembers.ShowMembersUseCase
import com.lttrung.notepro.ui.showmembers.ShowMembersUseCaseImpl
import com.lttrung.notepro.ui.viewprofile.ViewProfileUseCase
import com.lttrung.notepro.ui.viewprofile.ViewProfileUseCaseImpl
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
    abstract fun bindsMainUseCase(impl: MainUseCaseImpl): MainUseCase

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
    abstract fun bindsLoginRepositories(impl: LoginRepositoriesImpl): LoginRepositories

    @Binds
    abstract fun bindsNoteRepositories(impl: NoteRepositoriesImpl): NoteRepositories

    @Binds
    abstract fun bindsMemberRepositories(impl: MemberRepositoriesImpl): MemberRepositories

    @Binds
    abstract fun bindsUserRepositories(impl: UserRepositoriesImpl): UserRepositories

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