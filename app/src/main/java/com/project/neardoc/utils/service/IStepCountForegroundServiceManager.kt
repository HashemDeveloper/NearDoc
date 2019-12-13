package com.project.neardoc.utils.service

interface IStepCountForegroundServiceManager {
    fun startForegroundService()
    fun unBindOnActivityStart(isForegroundServiceAvailable: Boolean)
    fun bindOnActivityStop(isForegroundServiceAvailable: Boolean)
    fun stopBindService(isForegroundServiceAvailable: Boolean)
}