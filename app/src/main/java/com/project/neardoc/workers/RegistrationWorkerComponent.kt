package com.project.neardoc.workers

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface RegistrationWorkerComponent: AndroidInjector<RegistrationWorker> {
    @Subcomponent.Builder
    abstract class Builder: AndroidInjector.Builder<RegistrationWorker>()
}