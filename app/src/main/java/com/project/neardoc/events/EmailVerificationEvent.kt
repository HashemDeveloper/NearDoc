package com.project.neardoc.events

class EmailVerificationEvent(private val email: String) {
    fun getEmail(): String {
        return this.email
    }
}