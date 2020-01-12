package com.project.neardoc.workers

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface LoginWorkerComponent: AndroidInjector<LoginWorker> {
    @Subcomponent.Builder
    abstract class Builder: AndroidInjector.Builder<LoginWorker>()
}