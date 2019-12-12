package com.project.neardoc.utils.notifications

interface INotificationChanelBuilder {
    fun createNotificationChannels(notificationType: NotificationType, chanelId: String)
}