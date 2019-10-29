package com.project.neardoc.worker

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface UpdateEmailWorkerComponent: AndroidInjector<UpdateEmailWorker> {
    @Subcomponent.Builder
    abstract class Builder: AndroidInjector.Builder<UpdateEmailWorker>()
}