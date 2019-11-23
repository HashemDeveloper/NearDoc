package com.project.neardoc

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import androidx.work.Configuration
import androidx.work.Worker
import com.project.neardoc.di.ApplicationInjector
import com.project.neardoc.di.workermanager.HasWorkerInjector
import dagger.android.*
import javax.inject.Inject

class NearDocApp: Application(), HasActivityInjector, HasBroadcastReceiverInjector, HasWorkerInjector, Configuration.Provider, HasServiceInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
    @Inject
    lateinit var receiverInjector: DispatchingAndroidInjector<BroadcastReceiver>
    @Inject
    lateinit var dispatchingWorkerInjector: DispatchingAndroidInjector<Worker>
    @Inject
    lateinit var dispatchServiceInjector: DispatchingAndroidInjector<Service>

    override fun onCreate() {
        super.onCreate()
        ApplicationInjector.init(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
       return this.dispatchingAndroidInjector
    }
    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> {
        return this.receiverInjector
    }

    override fun getWorkManagerConfiguration(): Configuration {
       return Configuration.Builder()
           .setMinimumLoggingLevel(Log.INFO)
           .build()
    }

    override fun workerInjector(): AndroidInjector<Worker> {
        return this.dispatchingWorkerInjector
    }

    override fun serviceInjector(): AndroidInjector<Service> {
        return this.dispatchServiceInjector
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}