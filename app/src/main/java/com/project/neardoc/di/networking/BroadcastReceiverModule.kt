package com.project.neardoc.di.networking

import com.project.neardoc.broadcast.ConnectionBroadcastReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BroadcastReceiverModule {
    @ContributesAndroidInjector
    abstract fun contributesConnectionBroadcastReceiver() : ConnectionBroadcastReceiver
}