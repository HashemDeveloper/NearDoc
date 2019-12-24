package com.project.neardoc.workers

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface DeleteUserInfoWorkerComponent: AndroidInjector<DeleteUserInfoWorker> {
    @Subcomponent.Builder
    abstract class Builder: AndroidInjector.Builder<DeleteUserInfoWorker>()
}