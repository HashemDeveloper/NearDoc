package com.project.neardoc.data.local.remote

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
object NearDocRemoteRepoModule {
    @Singleton
    @Provides
    @JvmStatic
    internal fun providesNearDocRemoteRepo(retrofit: Retrofit): INearDocRemoteApi {
        return retrofit.create(INearDocRemoteApi::class.java)
    }
}