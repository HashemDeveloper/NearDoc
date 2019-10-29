package com.project.neardoc.di.firebaseservice

import com.google.firebase.messaging.FirebaseMessagingService
import com.project.neardoc.di.scopes.FirebaseServiceKey
import com.project.neardoc.services.FirebaseTokeIdServiceComponent
import com.project.neardoc.services.FirebaseTokenIdService
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap

@Module(subcomponents = [FirebaseTokeIdServiceComponent::class])
abstract class FirebaseServiceModule {
    @Binds
    @IntoMap
    @FirebaseServiceKey(FirebaseTokenIdService::class)
    abstract fun buildFirebaseServiceFactory(firebaseTokeIdServiceComponent: FirebaseTokeIdServiceComponent.Builder): AndroidInjector.Factory<out FirebaseMessagingService>
}