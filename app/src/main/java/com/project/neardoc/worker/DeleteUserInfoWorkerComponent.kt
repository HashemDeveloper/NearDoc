package com.project.neardoc.worker

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface DeleteUserInfoWorkerComponent: AndroidInjector<DeleteUserInfoWorker> {
    @Subcomponent.Builder
    abstract class Builder: AndroidInjector.Builder<DeleteUserInfoWorker>()
}