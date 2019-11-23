package com.project.neardoc.di.backgroundservice

import android.app.IntentService
import android.app.Service
import dagger.android.AndroidInjector

interface HasServiceInjector {
    fun serviceInjector(): AndroidInjector<IntentService>
}