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
    internal fun providesNearDocRemoteRepo(retrofit: Retrofit): INearDocRemoteRepo {
        return retrofit.create(INearDocRemoteRepo::class.java)
    }
}