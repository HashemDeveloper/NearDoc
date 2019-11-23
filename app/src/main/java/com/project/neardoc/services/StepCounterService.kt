package com.project.neardoc.services

import android.app.IntentService
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.project.neardoc.di.backgroundservice.ServiceInjection
import com.project.neardoc.utils.IStepCountSensor
import javax.inject.Inject

class StepCounterService @Inject constructor(): IntentService(StepCounterService::class.java.canonicalName) {
    @Inject
    lateinit var iStepCounterSensor: IStepCountSensor

    override fun onCreate() {
        ServiceInjection.inject(this)
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
       return Binder()
    }

    override fun onHandleIntent(intent: Intent?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}