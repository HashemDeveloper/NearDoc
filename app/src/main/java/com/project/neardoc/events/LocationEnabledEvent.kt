package com.project.neardoc.events

class LocationEnabledEvent constructor(private val isEnable: Boolean) {
    fun getIsEnabled(): Boolean {
        return this.isEnable
    }
}