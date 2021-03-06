package com.project.neardoc.workers

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface DeleteUsernameWorkerComponent: AndroidInjector<DeleteUsernameWorker> {
    @Subcomponent.Builder
    abstract class Builder: AndroidInjector.Builder<DeleteUsernameWorker>()
}