package com.project.neardoc.di.networking

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [FirebaseAuthNetServiceModule::class])
object NearDocNetServiceModule {
    @Singleton
    @Provides
    @JvmStatic
    internal fun provideGson(): Gson{
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
            .create()
    }
    @Singleton
    @Provides
    @JvmStatic
    internal fun provideRetrofit(gson: Gson, @Named("base_url") baseUrl: String): Retrofit{
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUrl)
            .build()
    }
}