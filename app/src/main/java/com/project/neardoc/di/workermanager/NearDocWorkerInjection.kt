package com.project.neardoc.di.workermanager

import androidx.work.Worker
import dagger.internal.Preconditions

class NearDocWorkerInjection {
    companion object {
       fun inject(worker: Worker) {
           Preconditions.checkNotNull(worker, "worker")
           val application = worker.applicationContext
           if (application !is HasWorkerInjector) {
               throw RuntimeException(
                   String.format(
                       "%s does not implements %s",
                       application.javaClass.canonicalName,
                       HasWorkerInjector::class.java.canonicalName
                   )
               )
           }
           val workerAndroidInjector = (application as HasWorkerInjector).workerInjector()
           Preconditions.checkNotNull(
               workerAndroidInjector,
               "%s workerInjector() returned null",
               application.javaClass
           )
           workerAndroidInjector.inject(worker)
       }
    }
}