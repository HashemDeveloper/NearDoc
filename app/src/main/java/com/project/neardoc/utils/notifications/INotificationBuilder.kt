package com.project.neardoc.utils.notifications

interface INotificationBuilder {
    fun createNotification(notificationType: NotificationType, requestCode: Int, chanelId: String, notificationId: Int, smallIcon: Int, bigIcon: Int, title: String, description: String, caloriesBurnedResult: Int)
}