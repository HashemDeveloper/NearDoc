package com.project.neardoc.utils

class Constants {
    companion object {
        val CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
        val GLOBAL_SHARED_PREF = "global_shared_pref"
        val SIGN_IN_PROVIDER_GOOGLE = "google"
        val WORKER_IMAGE_PATH = "imagePath"
        val WORKER_DISPLAY_NAME = "displayName"
        val WORKER_DB_AUTH_KEY = "dbAuthKey"
        val WORKER_EMAIL = "email"

        fun encodeUserEmail(email: String): String {
            return email.replace(".", ",")
        }
    }
}