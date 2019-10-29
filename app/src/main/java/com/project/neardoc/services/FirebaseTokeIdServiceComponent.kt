package com.project.neardoc.services

import dagger.Subcomponent
import dagger.android.AndroidInjector

@Subcomponent
interface FirebaseTokeIdServiceComponent: AndroidInjector<FirebaseTokenIdService> {
    @Subcomponent.Builder
    abstract class Builder: AndroidInjector.Builder<FirebaseTokenIdService>()
}