package com.project.neardoc.events

class UserStateEvent constructor(val isLoggedIn: Boolean) {
    fun getIsLoggedIn(): Boolean {
        return this.isLoggedIn
    }
}