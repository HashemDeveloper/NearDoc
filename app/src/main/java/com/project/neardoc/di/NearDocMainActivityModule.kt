package com.project.neardoc.di

import com.project.neardoc.NearDocMainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class NearDocMainActivityModule {
    @ContributesAndroidInjector(modules = [FragmentBuilderModule::class])
    abstract fun contributeNearDocMainActivity(): NearDocMainActivity
}