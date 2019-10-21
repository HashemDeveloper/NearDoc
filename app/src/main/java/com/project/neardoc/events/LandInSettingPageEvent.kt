package com.project.neardoc.events

class LandInSettingPageEvent constructor(private val isOnSettingPage: Boolean) {
    fun getIsOnSettingPage(): Boolean {
        return this.isOnSettingPage
    }
}