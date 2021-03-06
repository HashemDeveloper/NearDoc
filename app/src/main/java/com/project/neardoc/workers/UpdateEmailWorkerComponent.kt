package com.project.neardoc.workers

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface UpdateEmailWorkerComponent: AndroidInjector<UpdateEmailWorker> {
    @Subcomponent.Builder
    abstract class Builder: AndroidInjector.Builder<UpdateEmailWorker>()
}