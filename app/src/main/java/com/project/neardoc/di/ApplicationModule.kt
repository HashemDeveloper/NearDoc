package com.project.neardoc.di

import android.content.Context
import com.project.neardoc.NearDocApp
import com.project.neardoc.rxauth.IRxAuthentication
import com.project.neardoc.rxauth.RxAuthentication
import com.project.neardoc.utils.ConnectionStateMonitor
import com.project.neardoc.utils.IConnectionStateMonitor
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
    @Singleton
    @Provides
    fun provideRxAuthentication(rxAuthentication: RxAuthentication): IRxAuthentication{
        return rxAuthentication
    }
    @Singleton
    @Provides
    fun provideIConnectionStateMonitor(connectionStateMonitor: ConnectionStateMonitor): IConnectionStateMonitor {
        return connectionStateMonitor
    }
}