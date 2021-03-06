package com.project.neardoc.di

import com.project.neardoc.NearDocApp
import com.project.neardoc.di.backgroundservice.ServiceBuilderModule
import com.project.neardoc.di.networking.BroadcastReceiverModule
import com.project.neardoc.di.networking.NearDocNetServiceModule
import com.project.neardoc.di.viewmodel.ViewModelModule
import com.project.neardoc.di.workermanager.NearDocWorkerInjectionModule
import com.project.neardoc.di.workermanager.WorkerModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, NearDocMainActivityModule::class, ApplicationModule::class, NearDocNetServiceModule::class,
ViewModelModule::class, GoogleApiModule::class, BroadcastReceiverModule::class, NearDocWorkerInjectionModule::class, WorkerModule::class, ServiceBuilderModule::class])
interface ApplicationComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun buildApplication(nearDocApp: NearDocApp): Builder
        fun build(): ApplicationComponent
    }
    fun inject(nearDocApp: NearDocApp)
}