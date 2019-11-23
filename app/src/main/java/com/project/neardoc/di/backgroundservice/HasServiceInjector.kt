package com.project.neardoc.di.backgroundservice

import android.app.Service
import dagger.android.AndroidInjector

interface HasServiceInjector {
    fun serviceInjector(): AndroidInjector<Service>
}