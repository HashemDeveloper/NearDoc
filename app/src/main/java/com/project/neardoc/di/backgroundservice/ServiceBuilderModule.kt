package com.project.neardoc.di.backgroundservice

import com.project.neardoc.di.scopes.ServiceKey
import com.project.neardoc.services.StepCounterService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceBuilderModule {
    @ServiceKey
    @ContributesAndroidInjector
    abstract fun contributeServiceModule(): StepCounterService
}