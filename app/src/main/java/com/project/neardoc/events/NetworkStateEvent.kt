package com.project.neardoc.events

import com.project.neardoc.utils.networkconnections.NearDocNetworkType

class NetworkStateEvent(private val networkAvailable: Boolean, private val type: NearDocNetworkType?) {
    fun getIsNetworkAvailable(): Boolean {
        return this.networkAvailable
    }
    fun getNetworkType(): NearDocNetworkType? {
        return this.type
    }
}