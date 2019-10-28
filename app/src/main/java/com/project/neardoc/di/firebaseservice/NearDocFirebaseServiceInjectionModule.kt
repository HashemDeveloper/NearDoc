package com.project.neardoc.di.firebaseservice

import com.google.firebase.messaging.FirebaseMessagingService
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.Multibinds

@Module
abstract class NearDocFirebaseServiceInjectionModule {
    @Multibinds
    abstract fun firebaseServiceFactory(): Map<Class<out FirebaseMessagingService>, AndroidInjector.Factory<out FirebaseMessagingService>>
}