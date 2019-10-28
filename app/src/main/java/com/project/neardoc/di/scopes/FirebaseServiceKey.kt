package com.project.neardoc.di.scopes

import com.google.firebase.messaging.FirebaseMessagingService
import dagger.MapKey
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.PROPERTY_GETTER)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class FirebaseServiceKey(val value: KClass<out FirebaseMessagingService>)