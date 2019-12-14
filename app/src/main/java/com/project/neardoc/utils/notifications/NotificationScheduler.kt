package com.project.neardoc.utils.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.project.neardoc.broadcast.NearDocBroadcastReceiver
import com.project.neardoc.utils.Constants
import java.util.*
import javax.inject.Inject


class NotificationScheduler @Inject constructor(private val context: Context) :
    INotificationScheduler {

    override fun scheduleJob(action: String, requestCode: Int,  min: Int, hr: Int, result: Int) {
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hr)
        calendar.set(Calendar.MINUTE, min)
        calendar.set(Calendar.SECOND, 0)
        cancelJob(action, requestCode)
        val broadCastIntent = Intent(this.context, NearDocBroadcastReceiver::class.java)
            .putExtra(Constants.STEP_COUNT_VALUE, result)
            .setAction(action)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            this.context,
            requestCode,
            broadCastIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager: AlarmManager =
            this.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    override fun cancelJob(action: String, requestCode: Int) {
        val broadCastIntent = Intent()
        broadCastIntent.action = action
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            this.context,
            requestCode,
            broadCastIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager: AlarmManager =
            this.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}