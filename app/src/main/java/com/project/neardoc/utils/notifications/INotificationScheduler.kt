package com.project.neardoc.utils.notifications

import kotlin.reflect.KClass

interface INotificationScheduler {
    fun scheduleJob(action: String, requestCode: Int,  min: Int, hr: Int, result: Int)
    fun cancelJob(action: String, requestCode: Int)
}