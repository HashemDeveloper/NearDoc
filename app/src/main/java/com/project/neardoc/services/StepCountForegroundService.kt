package com.project.neardoc.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import javax.inject.Inject

class StepCountForegroundService @Inject constructor(): Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}