package com.project.neardoc.events

class SetupStepCountNotificationEvent constructor(private val isSetup: Boolean) {
    fun getIsSetup(): Boolean {
        return this.isSetup
    }
}