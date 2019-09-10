package com.project.neardoc.di

import com.project.neardoc.view.fragments.Registration
import com.project.neardoc.view.fragments.Welcome
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilderModule {
    @ContributesAndroidInjector
    abstract fun contributeWelcomeFragment(): Welcome
    @ContributesAndroidInjector
    abstract fun contributeRegistration(): Registration
}