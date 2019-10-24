package com.project.neardoc.di.workermanager

import androidx.work.Worker
import com.project.neardoc.di.scopes.WorkerKey
import com.project.neardoc.worker.*
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = [LoginWorkerComponent::class, RegistrationWorkerComponent::class, FetchUserWorkerComponent::class])
abstract class WorkerModule {
    @Binds
    @IntoMap
    @WorkerKey(LoginWorker::class)
    abstract fun buildLoginWorkerFactory(loginWorkerComponent: LoginWorkerComponent.Builder): AndroidInjector.Factory<out Worker>
    @Binds
    @IntoMap
    @WorkerKey(RegistrationWorker::class)
    abstract fun buildRegistrationWorkerFactory(registerWorkerComponent: RegistrationWorkerComponent.Builder): AndroidInjector.Factory<out Worker>
    @Binds
    @IntoMap
    @WorkerKey(FetchUserWorker::class)
    abstract fun buildHomepageWorkerFactory(fetchUserWorkerComponent: FetchUserWorkerComponent.Builder): AndroidInjector.Factory<out Worker>
}