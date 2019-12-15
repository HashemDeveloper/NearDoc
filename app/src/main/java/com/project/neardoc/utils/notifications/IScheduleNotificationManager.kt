package com.project.neardoc.utils.notifications

interface IScheduleNotificationManager {
    suspend fun scheduleRegularNotification()
    fun onCleared()
}