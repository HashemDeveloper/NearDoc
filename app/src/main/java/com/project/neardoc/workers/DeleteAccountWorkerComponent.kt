package com.project.neardoc.workers

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface DeleteAccountWorkerComponent: AndroidInjector<DeleteAccountWorker> {
    @Subcomponent.Builder
    abstract class Builder: AndroidInjector.Builder<DeleteAccountWorker>()
}