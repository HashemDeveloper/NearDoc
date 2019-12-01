package com.project.neardoc.utils

interface INotificationBuilder {
    fun createNotification(requestCode: Int, chanelId: String, notificationId: Int, smallIcon: Int, bigIcon: Int, title: String, description: String, caloriesBurnedResult: Int)
}