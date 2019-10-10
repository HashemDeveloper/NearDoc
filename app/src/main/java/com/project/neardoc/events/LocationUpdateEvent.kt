package com.project.neardoc.events

class LocationUpdateEvent(private val latitude: String, private val longitude: String) {
    fun getLatitude(): String {
        return this.latitude
    }
    fun getLongitude(): String {
        return this.longitude
    }
}