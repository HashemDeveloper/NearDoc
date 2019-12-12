package com.project.neardoc.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.project.neardoc.utils.notifications.INotificationBuilder
import javax.inject.Inject

class StepCountForegroundService @Inject constructor(): Service() {
    @Inject
    lateinit var iNotificationBuilder: INotificationBuilder
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}