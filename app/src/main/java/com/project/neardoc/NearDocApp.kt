package com.project.neardoc

import android.app.Activity
import android.app.Application
import android.content.BroadcastReceiver
import com.project.neardoc.di.ApplicationInjector
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasBroadcastReceiverInjector
import javax.inject.Inject

class NearDocApp: Application(), HasActivityInjector, HasBroadcastReceiverInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>
    @Inject
    lateinit var receiverInjector: DispatchingAndroidInjector<BroadcastReceiver>

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
}