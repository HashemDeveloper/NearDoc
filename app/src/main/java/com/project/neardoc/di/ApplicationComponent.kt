package com.project.neardoc.di

import com.project.neardoc.NearDocApp
import dagger.BindsInstance
import dagger.Component
import dagger.Subcomponent
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, NearDocMainActivityModule::class, ApplicationModule::class])
interface ApplicationComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun buildApplication(nearDocApp: NearDocApp): Builder
        fun build(): ApplicationComponent
    }
    fun inject(nearDocApp: NearDocApp)
}