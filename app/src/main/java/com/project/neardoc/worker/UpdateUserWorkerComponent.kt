package com.project.neardoc.worker

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface UpdateUserWorkerComponent: AndroidInjector<UpdateUserInfoWorker> {
    @Subcomponent.Builder
    abstract class Builder: AndroidInjector.Builder<UpdateUserInfoWorker>()
}