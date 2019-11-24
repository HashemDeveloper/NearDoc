package com.project.neardoc.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.project.neardoc.broadcast.NearDocBroadcastReceiver
import com.project.neardoc.utils.Constants
import com.project.neardoc.utils.IStepCountSensor
import dagger.android.AndroidInjection
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class StepCounterService @Inject constructor(): IntentService(StepCounterService::class.java.canonicalName) {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    @Inject
    lateinit var iStepCounterSensor: IStepCountSensor
    @Inject
    lateinit var nearDocBroadcastReceiver: NearDocBroadcastReceiver
    @Inject
    lateinit var context: Context

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
       return Binder()
    }

    override fun onHandleIntent(intent: Intent?) {
        val stepCounterIntent = Intent()
        stepCounterIntent.action = Constants.STEP_COUNTER_SERVICE_ACTION

        sendBroadcast(stepCounterIntent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        this.context.registerReceiver(this.nearDocBroadcastReceiver, IntentFilter(Constants.STEP_COUNTER_SERVICE_ACTION))
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}