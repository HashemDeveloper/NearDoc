package com.project.neardoc.utils.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.project.neardoc.R
import javax.inject.Inject

class NotificationChanelBuilder @Inject constructor(): INotificationChanelBuilder {
    @Inject
    lateinit var context: Context

    override fun createNotificationChannels(notificationType: NotificationType, chanelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager = this.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            var importance: Int?
            var chanel: NotificationChannel?
            var chanelName: String
            notificationManager.let {
                when (notificationType) {
                    NotificationType.NOTIFICATION_REGULAR -> {
                        importance = NotificationManager.IMPORTANCE_HIGH
                        chanelName = context.getString(R.string.step_count_chanel_name)
                        chanel = NotificationChannel(chanelId, chanelName, importance!!)
                        chanel!!.lightColor = ContextCompat.getColor(this.context, R.color.blue_gray_500)
                        chanel!!.setBypassDnd(true)
                        chanel!!.setShowBadge(true)
                        chanel!!.description = context.getString(R.string.turn_on_step_count_notification_message)
                        chanel!!.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                        notificationManager.createNotificationChannel(chanel!!)
                        return@let
                    }
                    NotificationType.NOTIFICATION_FOREGROUND -> {
                        importance = NotificationManager.IMPORTANCE_DEFAULT
                        chanelName = context.getString(R.string.step_count_chanel_name_foreground)
                        chanel = NotificationChannel(chanelId, chanelName, importance!!)
                        chanel!!.lightColor = ContextCompat.getColor(this.context, R.color.blue_gray_800)
                        chanel!!.description = context.getString(R.string.step_count_foreground_chanel_desc)
                        notificationManager.createNotificationChannel(chanel!!)
                        return@let
                    }
                }
            }
        }
    }
}