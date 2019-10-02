package com.project.neardoc.di.networking

import com.project.neardoc.data.local.remote.NearDocRemoteRepoModule
import dagger.Module
import dagger.Provides
import okhttp3.Call
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [NearDocRemoteRepoModule::class])
object FirebaseAuthNetServiceModule {
    @Singleton
    @Provides
    @JvmStatic
    internal fun provideOkHttpClient(): Call.Factory{
        return OkHttpClient.Builder()
            .build()
    }
    @Provides
    @Named("base_url")
    @JvmStatic
    internal fun provideBaseUrl(): String {
        return "https://neardoc-af101.firebaseio.com/"
    }
}