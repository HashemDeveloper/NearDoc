package com.project.neardoc.di.networking

import android.util.Log
import com.project.neardoc.BuildConfig
import com.project.neardoc.data.local.remote.NearDocRemoteRepoModule
import dagger.Module
import dagger.Provides
import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [NearDocRemoteRepoModule::class])
object FirebaseAuthNetServiceModule {
    @Singleton
    @Provides
    @JvmStatic
    internal fun provideOkHttpClient(): Call.Factory{
        val cookieManager = CookieManager()
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL)
        return OkHttpClient.Builder()
            .readTimeout(10000, TimeUnit.MILLISECONDS)
            .connectTimeout(10000, TimeUnit.MILLISECONDS)
            .addInterceptor { chain: Interceptor.Chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Host", "api.betterdoctor.com")
                    .addHeader("Content-Type", "application/json;charset=UTF-8")
                    .method(original.method(), original.body())
                    .build()
                val response = chain.proceed(request)
                if (BuildConfig.DEBUG) {
                    Log.d("NearDocNetworking-->>", "Code : " + response.code())
                }
                try {
                    if (response.code() == 401) {
                        return@addInterceptor response
                    }
                } catch (e: Exception) {
                    Log.i("RetrofitError: ", e.localizedMessage!!)
                } finally {
                    if (response.body() != null) {
                        response.body()!!.close()
                    }
                }
                response
            }
            .build()
    }
    @Provides
    @Named("base_url")
    @JvmStatic
    internal fun provideBaseUrl(): String {
        return "https://api.betterdoctor.com/2016-03-01/"
    }
}