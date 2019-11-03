package com.project.neardoc.worker

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface UpdatePasswordWorkerComponent: AndroidInjector<UpdatePasswordWorker> {
    @Subcomponent.Builder
    abstract class Builder: AndroidInjector.Builder<UpdatePasswordWorker>()
}
