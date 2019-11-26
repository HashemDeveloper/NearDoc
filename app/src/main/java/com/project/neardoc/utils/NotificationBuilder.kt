package com.project.neardoc.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.project.neardoc.NearDocMainActivity
import javax.inject.Inject

class NotificationBuilder @Inject constructor(): INotificationBuilder {

    @Inject
    lateinit var context: Context

    override fun createNotification(requestCode: Int, chanelId:
    String, notificationId: Int, smallIcon: Int, bigIcon: Int, title: String, description: String) {
        createNotificationChanel(chanelId)
        val intent: Intent = Intent(this.context, NearDocMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this.context, requestCode, intent, 0)
        val bigPicture: Bitmap = BitmapFactory.decodeResource(this.context.resources, bigIcon)
        val notification: Notification = NotificationCompat.Builder(this.context, chanelId)
            .setSmallIcon(smallIcon)
            .setLargeIcon(bigPicture)
            .setContentTitle(title)
            .setContentText(description)
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(bigPicture)
                .bigLargeIcon(null))
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        NotificationManagerCompat.from(this.context).notify(notificationId, notification)
    }
    private fun createNotificationChanel(chanelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: String = chanelId
            val desc = "Chanel Description"
            val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
            val chanel: NotificationChannel = NotificationChannel(chanelId, name, importance).apply {
                description = desc
            }
            val notificationManager: NotificationManager = this.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(chanel)
        }
    }
}