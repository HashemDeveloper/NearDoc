package com.project.neardoc.di.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.neardoc.di.scopes.ViewModelKey
import com.project.neardoc.viewmodel.ForgotPasswordViewModel
import com.project.neardoc.viewmodel.SearchPageViewModel
import com.project.neardoc.viewmodel.LoginViewModel
import com.project.neardoc.viewmodel.RegistrationViewModel
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
}