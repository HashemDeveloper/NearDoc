package com.project.neardoc.di.backgroundservice

import android.app.Service
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.Multibinds

@Module
abstract class ServiceInjectionModule {
    @Multibinds
    abstract fun serviceFactory(): Map<Class<out Service>, AndroidInjector.Factory<out Service>>
}