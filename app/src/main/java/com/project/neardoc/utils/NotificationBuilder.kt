package com.project.neardoc.utils

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
import androidx.navigation.NavController
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.Navigation
import com.project.neardoc.NearDocMainActivity
import com.project.neardoc.R
import com.project.neardoc.view.fragments.AccountPage
import javax.inject.Inject

class NotificationBuilder @Inject constructor(): INotificationBuilder {

    @Inject
    lateinit var context: Context

    override fun createNotification(requestCode: Int, chanelId:
    String, notificationId: Int, smallIcon: Int, bigIcon: Int, title: String, description: String) {
        createNotificationChanel(chanelId)
        val data = Bundle()
        data.putString(Constants.STEP_COUNT_NOTIFICATION, "STEP_NOTIFICATION")
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
    private fun createNotificationChanel(chanelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: String = chanelId
            val desc = "Chanel Description"
            val importance: Int = NotificationManager.IMPORTANCE_HIGH
            val chanel: NotificationChannel = NotificationChannel(chanelId, name, importance).apply {
                description = desc
            }
            val notificationManager: NotificationManager = this.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(chanel)
        }
    }
}