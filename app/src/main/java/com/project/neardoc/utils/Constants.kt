package com.project.neardoc.utils

import android.util.Patterns
import java.util.regex.Matcher
import java.util.regex.Pattern

class Constants {
    companion object {

        val SHARED_PREF_GOOGLE_TOKEN_ID: String = "GOOGLE_TOKEN_ID"
        val SHARED_PREF_GOOGLE_TOKEN_ENCRYPT_IV = "GOOGLE_TOKEN_ENCRYPT_IV"
        val SHARED_PREF_USER_LOGIN_PROVIDER: String = "LOGIN_PROVIDER"
        val CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
        val USER_STATE_ACTION = "USER_STATE_ACTION"
        val LOCATION_SERVICE_ACTION = "ACTION_LOCATION_SERVICE"
        val SIGN_IN_PROVIDER_GOOGLE = "google"
        val SIGN_IN_PROVIDER_FIREBASE = "firebase"
        val WORKER_FULL_NAME = "fullName"
        val WORKER_IMAGE_PATH = "imagePath"
        val WORKER_DISPLAY_NAME = "displayName"
        val WORKER_DB_AUTH_KEY = "dbAuthKey"
        val WORKER_NEW_EMAIL = "newEmail"
        val WORKER_EMAIL = "email"
        val WORKER_WEB_KEY = "webKey"
        val WORKER_PASSWORD = "password"
        val WORKER_NEW_PASS = "newPass"
        val WORKER_TOKEN_ID = "tokenId"
        val WORKER_ERROR_DATA = "errorData"
        val WORKER_OLD_EMAIL = "oldEmail"
        val SEARCH_DOC_BY_DISEASE_ENDPOINT = "https://api.betterdoctor.com/2016-03-01/doctors"
        val BETTER_DOC_API_HEALTH_ENDPOINT = "https://api.betterdoctor.com/2016-03-01/info"
        val BETTER_DOC_KNOWN_CONDITIONS = "https://api.betterdoctor.com/2016-03-01/conditions"
        val FIREBASE_AUTH_SIGN_UP_ENDPOINT = "https://identitytoolkit.googleapis.com/v1/accounts:signUp"
        val FIREBASE_AUTH_EMAIL_VERIFICATION_ENDPOINT = "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode"
        val FIREBASE_AUTH_PASSWORD_RESET_ENDPOINT = "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode"
        val FIREBASE_AUTH_UPDATE_LOGIN_INFO_END_POINT = "https://identitytoolkit.googleapis.com/v1/accounts:update"
        val FIREBASE_AUTH_FETCH_USER_INFO_END_POINT = "https://identitytoolkit.googleapis.com/v1/accounts:lookup"
        val FIREBASE_DELETE_ACOUNT = "https://identitytoolkit.googleapis.com/v1/accounts:delete"
        val FIREBASE_AUTH_PASSWORD_RESET_REQUEST_TYPE = "PASSWORD_RESET"
        val FIREBASE_EMAIL_VERFICATION_REQUEST_TYPE = "VERIFY_EMAIL"
        val SHARED_PREF_ID_TOKEN = "idToken"
        val SHARED_PREF_ENCRYPT_IV = "encryptIv"
        val FIREBASE_ID_TOKEN = "ID_TOKEN"
        val GOOGLE_ID_TOKEN = "GOOGLE_ID_TOKEN"
        val mobileData = "MOBILE_DATA"
        val wifiData = "WIFI_DATA"
        val SHARED_PREF_USER_IMAGE = "userImage"
        val SHARED_PREF_USER_NAME = "userName"
        val SHARED_PREF_USER_EMAIL = "userEmail"
        val SHARED_PREF_USER_USERNAME = "email"
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