package com.project.neardoc.di.networking

import dagger.Module
import dagger.Provides
import okhttp3.Call
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
object FirebaseAuthNetServiceModule {
    @Singleton
    @Provides
    @JvmStatic
    internal fun provideOkHttpClient(): Call.Factory{
        return OkHttpClient.Builder()
            .build()
    }
    @Singleton
    @Named("auth_base_url")
    @JvmStatic
    internal fun provideBaseUrl(): String {
        return ""
    }
}