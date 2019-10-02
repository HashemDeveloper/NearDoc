package com.project.neardoc.di.workermanager

import androidx.work.Worker
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.Multibinds

@Module
abstract class NearDocWorkerInjectionModule {
    @Multibinds
    abstract fun workerInjectionFactory(): Map<Class<out Worker>, AndroidInjector.Factory<out Worker>>
}