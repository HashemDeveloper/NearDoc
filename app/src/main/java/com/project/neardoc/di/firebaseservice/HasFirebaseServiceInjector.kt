package com.project.neardoc.di.firebaseservice

import com.google.firebase.messaging.FirebaseMessagingService
import dagger.android.AndroidInjector

interface HasFirebaseServiceInjector {
    fun firebaseServiceInjector(): AndroidInjector<FirebaseMessagingService>
}