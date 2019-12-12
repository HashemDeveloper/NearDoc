package com.project.neardoc.events

import com.project.neardoc.utils.widgets.PageType

class LandInSettingPageEvent constructor(private val isOnSettingPage: Boolean, private val pageType: PageType) {
    fun getIsOnSettingPage(): Boolean {
        return this.isOnSettingPage
    }
    fun getCurrentPage(): PageType {
        return this.pageType
    }
}