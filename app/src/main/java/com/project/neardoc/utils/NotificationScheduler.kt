package com.project.neardoc.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.project.neardoc.broadcast.NearDocBroadcastReceiver
import java.util.*
import javax.inject.Inject


class NotificationScheduler @Inject constructor(private val context: Context) :
    INotificationScheduler {

    override fun scheduleJob(action: String, requestCode: Int,  min: Int, hr: Int) {
        val calendar: Calendar = Calendar.getInstance()
        val setCalendar: Calendar = Calendar.getInstance()
        setCalendar.set(Calendar.HOUR_OF_DAY, hr)
        setCalendar.set(Calendar.MINUTE, min)
        setCalendar.set(Calendar.SECOND, 0)
        cancelJob(action, requestCode)
        if (setCalendar.before(calendar)) {
            setCalendar.add(Calendar.DATE, 1)
        }

        val broadCastIntent = Intent(this.context, NearDocBroadcastReceiver::class.java)
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
            1000L,
            1000L,
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