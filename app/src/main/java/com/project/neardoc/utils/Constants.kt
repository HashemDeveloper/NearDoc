package com.project.neardoc.utils

import android.util.Patterns
import java.util.regex.Matcher
import java.util.regex.Pattern

class Constants {
    companion object {
        const val CALORIES_BURNED_RESULT: String = "CALORIES_BURNED"
        const val NOTIFICATION_ENABLED: String = "NOTIFICATION_ENABLED"
        const val STEP_COUNT_NOTIFICATION = "STEP_COUNT_NOTIFICATION"
        const val STEP_COUNT_VALUE: String = "STEP_COUNT_VALUE"
        const val STEP_COUNTER_SERVICE_ACTION: String = "STEP_COUNTER_ACTION"
        const val SHARED_PREF_USER_CURRENT_STATE: String = "user_current_state"
        const val SHARED_PREF_BREATH_REPEAT_COUNT: String = "breath_repeat_count"
        const val SHARED_PREF_BREATHING_DATE: String = "breathing_date"
        const val SHARED_PREF_BREATHING_SESSION: String = "breathing_session"
        const val SHARED_PREF_BREATHING_NUM: String = "breathing"
        const val SHARED_PREF_SEARCH_LIMIT = "SEARCH_LIMIT"
        const val SHARED_PREF_DISTANCE_RADIUS = "DISTANCE_RADIUS"
        const val SHARED_PREF_IS_LOCATION_ENABLED = "LOCATION_ENABLED"
        const val WORKER_LOCATION_LAT = "lat"
        const val WORKER_LOCATION_LON = "lon"
        const val SHARED_PREF_GOOGLE_TOKEN_ID: String = "GOOGLE_TOKEN_ID"
        const val SHARED_PREF_GOOGLE_TOKEN_ENCRYPT_IV = "GOOGLE_TOKEN_ENCRYPT_IV"
        const val SHARED_PREF_USER_LOGIN_PROVIDER: String = "LOGIN_PROVIDER"
        const val CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
        const val USER_STATE_ACTION = "USER_STATE_ACTION"
        const val LOCATION_SERVICE_ACTION = "ACTION_LOCATION_SERVICE"
        const val SIGN_IN_PROVIDER_GOOGLE = "google"
        const val SIGN_IN_PROVIDER_FIREBASE = "firebase"
        const val WORKER_FULL_NAME = "fullName"
        const val WORKER_IMAGE_PATH = "imagePath"
        const val WORKER_DISPLAY_NAME = "displayName"
        const val WORKER_DB_AUTH_KEY = "dbAuthKey"
        const val WORKER_NEW_EMAIL = "newEmail"
        const val WORKER_EMAIL = "email"
        const val WORKER_WEB_KEY = "webKey"
        const val WORKER_PASSWORD = "password"
        const val WORKER_NEW_PASS = "newPass"
        const val WORKER_TOKEN_ID = "tokenId"
        const val WORKER_ERROR_DATA = "errorData"
        const val WORKER_OLD_EMAIL = "oldEmail"
        const val SEARCH_DOC_BY_DISEASE_ENDPOINT = "https://api.betterdoctor.com/2016-03-01/doctors"
        const val BETTER_DOC_API_HEALTH_ENDPOINT = "https://api.betterdoctor.com/2016-03-01/info"
        const val BETTER_DOC_INSURANCE_ENDPOINT = "https://api.betterdoctor.com/2016-03-01/insurances"
        const val BETTER_DOC_KNOWN_CONDITIONS = "https://api.betterdoctor.com/2016-03-01/conditions"
        const val FIREBASE_AUTH_SIGN_UP_ENDPOINT = "https://identitytoolkit.googleapis.com/v1/accounts:signUp"
        const val FIREBASE_AUTH_EMAIL_VERIFICATION_ENDPOINT = "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode"
        const val FIREBASE_AUTH_PASSWORD_RESET_ENDPOINT = "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode"
        const val FIREBASE_AUTH_UPDATE_LOGIN_INFO_END_POINT = "https://identitytoolkit.googleapis.com/v1/accounts:update"
        const val FIREBASE_AUTH_FETCH_USER_INFO_END_POINT = "https://identitytoolkit.googleapis.com/v1/accounts:lookup"
        const val FIREBASE_DELETE_ACOUNT = "https://identitytoolkit.googleapis.com/v1/accounts:delete"
        const val FIREBASE_AUTH_PASSWORD_RESET_REQUEST_TYPE = "PASSWORD_RESET"
        const val FIREBASE_EMAIL_VERFICATION_REQUEST_TYPE = "VERIFY_EMAIL"
        const val SHARED_PREF_ID_TOKEN = "idToken"
        const val SHARED_PREF_ENCRYPT_IV = "encryptIv"
        const val FIREBASE_ID_TOKEN = "ID_TOKEN"
        const val GOOGLE_ID_TOKEN = "GOOGLE_ID_TOKEN"
        const val mobileData = "MOBILE_DATA"
        const val wifiData = "WIFI_DATA"
        const val SHARED_PREF_USER_IMAGE = "userImage"
        const val SHARED_PREF_USER_NAME = "userName"
        const val SHARED_PREF_USER_EMAIL = "userEmail"
        const val SHARED_PREF_USER_USERNAME = "email"
        const val ENABLE_LOCATION_SWITCH = false
        const val DISABLE_SEND_FEEDBACK = false
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