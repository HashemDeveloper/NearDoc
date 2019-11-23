package com.project.neardoc.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.project.neardoc.di.backgroundservice.ServiceInjection
import javax.inject.Inject

class StepCounterService @Inject constructor(): Service() {

    override fun onCreate() {
        ServiceInjection.inject(this)
        super.onCreate()
    }

    override fun onBind(intent: Intent?): IBinder? {
       return null
    }
}