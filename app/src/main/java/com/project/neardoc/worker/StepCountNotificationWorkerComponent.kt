package com.project.neardoc.worker

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface StepCountNotificationWorkerComponent: AndroidInjector<StepCountNotificationWorker> {
    @Subcomponent.Builder
    abstract class Builder: AndroidInjector.Builder<StepCountNotificationWorker>()
}