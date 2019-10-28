package com.project.neardoc.di.firebaseservice

import android.content.Context
import com.google.firebase.messaging.FirebaseMessagingService
import dagger.android.AndroidInjector
import dagger.internal.Preconditions

class NearFirebaseServiceInjection {
    companion object {
        fun inject(firebaseMessagingService: FirebaseMessagingService) {
            Preconditions.checkNotNull(firebaseMessagingService, "Firebase Message Service")
            val application: Context = firebaseMessagingService.applicationContext
            if (application !is HasFirebaseServiceInjector) {
                throw RuntimeException(
                    String.format(
                        "%s does not implements %s",
                        application.javaClass.canonicalName,
                        HasFirebaseServiceInjector::class.java.canonicalName
                    )
                )
            }
            val firebaesMessageServiceInjection: AndroidInjector<FirebaseMessagingService> = (application as HasFirebaseServiceInjector).firebaseServiceInjector()
            Preconditions.checkNotNull(firebaesMessageServiceInjection,
                "%s firebaseMessageServiceReturned() null",
                application.javaClass.canonicalName)
            firebaesMessageServiceInjection.inject(firebaseMessagingService)
        }
    }
}