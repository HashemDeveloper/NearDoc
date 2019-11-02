package com.project.neardoc.events

class LoginInfoUpdatedEvent constructor(private val isLoginInfoUpdated: Boolean) {
    fun getIsLoginInfoUpdated(): Boolean {
        return this.isLoginInfoUpdated
    }
}