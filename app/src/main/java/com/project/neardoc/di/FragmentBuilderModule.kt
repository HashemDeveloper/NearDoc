package com.project.neardoc.di

import com.project.neardoc.view.fragments.*
import com.project.neardoc.view.settings.*
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
    @ContributesAndroidInjector
    abstract fun contributeSignInAndSecurityFragment(): SignInSecurity
    @ContributesAndroidInjector
    abstract fun contributeContactUsFragment(): ContactUs
    @ContributesAndroidInjector
    abstract fun contributeTermsAndConditionPage(): TermsAndCondition
    @ContributesAndroidInjector
    abstract fun contributePrivacyPolicyPage(): PrivacyPolicy
    @ContributesAndroidInjector
    abstract fun contributeUpdateEmail(): UpdateEmail
    @ContributesAndroidInjector
    abstract fun contributeUpdatePassword(): UpdatePassword
    @ContributesAndroidInjector
    abstract fun contributeHeartBeat(): HeartBeat
    @ContributesAndroidInjector
    abstract fun contributeDoctorsDetails(): DoctorsDetails
    @ContributesAndroidInjector
    abstract fun contributeInsuranceList(): InsuranceList
}