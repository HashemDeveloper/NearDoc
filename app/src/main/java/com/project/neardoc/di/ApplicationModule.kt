package com.project.neardoc.di

import android.content.Context
import com.project.neardoc.NearDocApp
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule {
    @Singleton
    @Provides
    fun provideContext(nearDocApp: NearDocApp): Context {
        return nearDocApp
    }
}