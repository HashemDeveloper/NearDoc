package com.project.neardoc.di.workermanager

import androidx.work.Worker
import com.project.neardoc.di.scopes.WorkerKey
import com.project.neardoc.worker.LoginWorker
import com.project.neardoc.worker.LoginWorkerComponent
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = [LoginWorkerComponent::class])
abstract class WorkerModule {
    @Binds
    @IntoMap
    @WorkerKey(LoginWorker::class)
    abstract fun buildLoginWorkerFactory(loginWorkerComponent: LoginWorkerComponent.Builder): AndroidInjector.Factory<out Worker>
}