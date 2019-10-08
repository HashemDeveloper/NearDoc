package com.project.neardoc.utils


interface ILocationService {
    fun getObserver(): LocationService
    fun setPermissionListener(iPermissionListener: IPermissionListener)
    abstract fun registerBroadcastListener(b: Boolean)
}