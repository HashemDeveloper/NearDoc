package com.project.neardoc.utils.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.project.neardoc.NearDocMainActivity
import com.project.neardoc.R
import com.project.neardoc.utils.Constants
import javax.inject.Inject

class NotificationBuilder @Inject constructor():
    INotificationBuilder {

    @Inject
    lateinit var context: Context
    @Inject
    lateinit var iNotificationChanelBuilder: INotificationChanelBuilder

    override fun createNotification(notificationType: NotificationType, requestCode: Int, chanelId:
    String, notificationId: Int, smallIcon: Int, bigIcon: Int, title: String, description: String, caloriesBurnedResult: Int) {
        this.iNotificationChanelBuilder.createNotificationChannels(notificationType, chanelId)
        when (notificationType) {
            NotificationType.NOTIFICATION_REGULAR -> {
                val data = Bundle()
                data.putString(Constants.STEP_COUNT_NOTIFICATION, "STEP_NOTIFICATION")
                data.putInt(Constants.CALORIES_BURNED_RESULT, caloriesBurnedResult)
                val contentComponentName = ComponentName(this.context, NearDocMainActivity::class.java)
                val pendingIntent: PendingIntent = NavDeepLinkBuilder(this.context)
                    .setComponentName(contentComponentName)
                    .setGraph(R.navigation.nearby_doc_navigation)
                    .setDestination(R.id.accountPage)
                    .setArguments(data)
                    .createPendingIntent()
                val bigPicture: Bitmap = BitmapFactory.decodeResource(this.context.resources, bigIcon)
                val notification: Notification = NotificationCompat.Builder(this.context, chanelId)
                    .setSmallIcon(smallIcon)
                    .setLargeIcon(bigPicture)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setAutoCancel(true)
                    .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(description))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build()
                NotificationManagerCompat.from(this.context).notify(notificationId, notification)
            }
            NotificationType.NOTIFICATION_FOREGROUND -> {
                val data = Bundle()
                data.putString(Constants.STEP_COUNT_FOREGROUND, "STEP_COUNT_FOREGROUND")
                val pendingComponentName = ComponentName(this.context, NearDocMainActivity::class.java)
                val appName: String = this.context.getString(R.string.app_name)
                val pendingIntent: PendingIntent = NavDeepLinkBuilder(this.context)
                    .setComponentName(pendingComponentName)
                    .setArguments(data)
                    .setGraph(R.navigation.nearby_doc_navigation)
                    .setDestination(R.id.accountPage)
                    .createPendingIntent()
                val notification: Notification = NotificationCompat.Builder(this.context, chanelId)
                    .setSmallIcon(smallIcon)
                    .setContentTitle(appName)
                    .setContentText(context.getString(R.string.foreground_step_count_running_message))
                    .setTicker(this.context.getString(R.string.foreground_step_count_running_message))
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setWhen(System.currentTimeMillis())
                    .build()
                NotificationManagerCompat.from(this.context).notify(notificationId, notification)
            }
        }
    }
}