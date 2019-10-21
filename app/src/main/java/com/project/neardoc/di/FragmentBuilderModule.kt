package com.project.neardoc.di

import com.project.neardoc.view.fragments.*
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilderModule {
    @ContributesAndroidInjector
    abstract fun contributeWelcomeFragment(): Welcome
    @ContributesAndroidInjector
    abstract fun contributeRegistration(): Registration
    @ContributesAndroidInjector
    abstract fun contributeLogin(): Login
    @ContributesAndroidInjector
    abstract fun contributeForgotPasswordFragment(): ForgotPassword
    @ContributesAndroidInjector
    abstract fun contributeHomePageFragment(): HomePage
    @ContributesAndroidInjector
    abstract fun contributeSearchPageFragment(): SearchPage
    @ContributesAndroidInjector
    abstract fun contributeAccountPageFragment(): AccountPage
    @ContributesAndroidInjector
    abstract fun contributeSettingPageFragment(): SettingsFragment
}