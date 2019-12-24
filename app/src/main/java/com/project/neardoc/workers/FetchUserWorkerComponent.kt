package com.project.neardoc.workers

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface FetchUserWorkerComponent: AndroidInjector<FetchUserWorker> {
    @Subcomponent.Builder
    abstract class Builder: AndroidInjector.Builder<FetchUserWorker>()
}