package com.project.neardoc.di.backgroundservice

import android.app.IntentService
import android.app.Service
import android.content.Context
import dagger.android.AndroidInjector
import dagger.internal.Preconditions
import java.lang.RuntimeException

class ServiceInjection {
    companion object {
        fun inject(service: IntentService) {
            Preconditions.checkNotNull(service, "Service Error")
            val application: Context = service.applicationContext
            if (application !is HasServiceInjector) {
                throw RuntimeException(
                    String.format("%s does not implements %s",
                        application.javaClass.canonicalName,
                        HasServiceInjector::class.java.canonicalName)
                )
            }
            val serviceInjection: AndroidInjector<IntentService> = (application as HasServiceInjector).serviceInjector()
            Preconditions.checkNotNull("%s ServiceInjection returned null",
                application.javaClass.canonicalName)
            serviceInjection.inject(service)
        }
    }
}