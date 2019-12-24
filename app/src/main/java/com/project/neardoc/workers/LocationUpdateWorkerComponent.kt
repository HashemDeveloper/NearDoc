package com.project.neardoc.workers

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface LocationUpdateWorkerComponent: AndroidInjector<LocationUpdateWorker> {
    @Subcomponent.Builder
    abstract class Builder: AndroidInjector.Builder<LocationUpdateWorker>()
}