package com.project.neardoc.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.neardoc.di.scopes.ViewModelKey
import com.project.neardoc.viewmodel.SignInSecViewModel
import com.project.neardoc.viewmodel.*
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun provideViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    internal abstract fun provideViewModelForLogin(loginViewModel: LoginViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(RegistrationViewModel::class)
    internal abstract fun provideViewModelForRegistration(registrationViewModel: RegistrationViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(ForgotPasswordViewModel::class)
    internal abstract fun provideViewModelForForgotPassword(forgotPasswordViewModel: ForgotPasswordViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(SearchPageViewModel::class)
    internal abstract fun provideViewModelForHomepage(searchPageViewModel: SearchPageViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(HomePageViewModel::class)
    internal abstract fun provideHomePageViewModel(homePageViewModel: HomePageViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(UpdateEmailViewModel::class)
    internal abstract fun provideUpdateEmailViewModel(updateEmailViewModel: UpdateEmailViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(UpdatePasswordViewModel::class)
    internal abstract fun provideUpdatePasswordViewModel(updatePasswordViewModel: UpdatePasswordViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(SignInSecViewModel::class)
    internal abstract fun provideSignInSecViewModel(signInSecViewModel: SignInSecViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(AccountPageViewModel::class)
    internal abstract fun provideAccountPageViewModel(accountPageViewModel: AccountPageViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(HeartBeatViewModel::class)
    internal abstract fun provideHeartBeatViewModel(heartBeatViewModel: HeartBeatViewModel): ViewModel
    @Binds
    @IntoMap
    @ViewModelKey(DoctorDetailsViewModel::class)
    internal abstract fun provideDoctorDetailsViewModel(doctorDetailsViewModel: DoctorDetailsViewModel): ViewModel
}