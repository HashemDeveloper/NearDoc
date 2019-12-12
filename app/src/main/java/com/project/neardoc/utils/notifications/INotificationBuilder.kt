package com.project.neardoc.utils.notifications

import android.app.Notification

interface INotificationBuilder {
    fun createNotification(notificationType: NotificationType, requestCode: Int, chanelId: String, smallIcon: Int, bigIcon: Int, title: String, description: String, caloriesBurnedResult: Int): Notification
}