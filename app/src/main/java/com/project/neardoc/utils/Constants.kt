package com.project.neardoc.utils

import android.util.Patterns
import java.util.regex.Matcher
import java.util.regex.Pattern

class Constants {
    companion object {
        val CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
        val GLOBAL_SHARED_PREF = "global_shared_pref"
        val SIGN_IN_PROVIDER_GOOGLE = "google"
        val WORKER_IMAGE_PATH = "imagePath"
        val WORKER_DISPLAY_NAME = "displayName"
        val WORKER_DB_AUTH_KEY = "dbAuthKey"
        val WORKER_EMAIL = "email"
        val FIREBASE_AUTH_SIGN_UP_ENDPOINT = "https://identitytoolkit.googleapis.com/v1/accounts:signUp"
        val FIREBASE_AUTH_EMAIL_VERIFICATION_ENDPOINT = "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode"
        val FIREBASE_EMAIL_VERFICATION_REQUEST_TYPE = "VERIFY_EMAIL"
        fun encodeUserEmail(email: String): String {
            return email.replace(".", ",")
        }
        fun usernameValidator(username: String): Boolean {
            val pattern: Pattern
            val matcher: Matcher
            val USERNAME_PATTERN = "^[a-z0-9_-]{3,12}$"
            pattern = Pattern.compile(USERNAME_PATTERN)
            matcher = pattern.matcher(username)
            return matcher.matches()
        }
        fun emailValidator(email: String): Boolean {
            val pattern: Pattern
            val matcher: Matcher
            val EMAIL_PATTERN = Patterns.EMAIL_ADDRESS.pattern()
            pattern = Pattern.compile(EMAIL_PATTERN)
            matcher = pattern.matcher(email)
            return matcher.matches()
        }
        fun passwordValidator(password: String): Boolean {
            val pattern: Pattern
            val matcher: Matcher
            val PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@.#$%]).{6,20})"
            pattern = Pattern.compile(PASSWORD_PATTERN)
            matcher = pattern.matcher(password)
            return matcher.matches()
        }
    }
}