package com.project.neardoc.utils


interface ILocationService {
    fun getObserver(): LocationService
    fun setPermissionListener(iPermissionListener: IPermissionListener)
    fun registerBroadcastListener(b: Boolean)
}