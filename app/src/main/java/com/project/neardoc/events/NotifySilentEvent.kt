package com.project.neardoc.events

class NotifySilentEvent constructor(private var hasNotification: Boolean) {
    fun getHasNotification(): Boolean {
        return this.hasNotification
    }
}