package com.project.neardoc.utils.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.project.neardoc.NearDocMainActivity
import com.project.neardoc.R
import com.project.neardoc.services.StepCountForegroundService
import com.project.neardoc.utils.Constants
import javax.inject.Inject

class NotificationBuilder @Inject constructor():
    INotificationBuilder {

    @Inject
    lateinit var context: Context
    @Inject
    lateinit var iNotificationChanelBuilder: INotificationChanelBuilder

    override fun createNotification(notificationType: NotificationType, requestCode: Int, chanelId:
    String, smallIcon: Int, bigIcon: Int, title: String, description: String, caloriesBurnedResult: Int): Notification {
        this.iNotificationChanelBuilder.createNotificationChannels(notificationType, chanelId)
        val notification: Notification
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
               val builder: NotificationCompat.Builder = getNotificationBuilder(chanelId)
                    .setSmallIcon(smallIcon)
                    .setLargeIcon(bigPicture)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setAutoCancel(true)
                    .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(description))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder.setChannelId(chanelId)
                }
                notification = builder.build()
                NotificationManagerCompat.from(context).notify(Constants.STEP_COUNT_REGULAR_NOTIFICATION_ID, notification)
                return notification
            }
            NotificationType.NOTIFICATION_FOREGROUND -> {
                val data = Bundle()
                data.putBoolean(Constants.STEP_COUNT_FOREGROUND, true)
                val pendingComponentName = ComponentName(this.context, NearDocMainActivity::class.java)
                val appName: String = this.context.getString(R.string.app_name)
                val intent = Intent(this.context, StepCountForegroundService::class.java)
                val servicePendingIntent: PendingIntent = PendingIntent.getService(this.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                val pendingIntent: PendingIntent = NavDeepLinkBuilder(this.context)
                    .setComponentName(pendingComponentName)
                    .setArguments(data)
                    .setGraph(R.navigation.nearby_doc_navigation)
                    .setDestination(R.id.accountPage)
                    .createPendingIntent()
                val builder: NotificationCompat.Builder = getNotificationBuilder(chanelId)
                    .addAction(R.drawable.ic_cancel, context.getString(R.string.stop_step_counter), servicePendingIntent)
                    .setSmallIcon(smallIcon)
                    .setContentTitle(appName)
                    .setContentText(context.getString(R.string.foreground_step_count_running_message))
                    .setTicker(this.context.getString(R.string.foreground_step_count_running_message))
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setWhen(System.currentTimeMillis())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder.setChannelId(chanelId)
                }
                notification = builder.build()
                return notification
            }
        }
    }
    private fun getNotificationBuilder(chanelId: String): NotificationCompat.Builder {
        val builder: NotificationCompat.Builder?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = NotificationCompat.Builder(this.context, chanelId)
        } else {
            builder = NotificationCompat.Builder(this.context)
        }
        return builder
    }
}