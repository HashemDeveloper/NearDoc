package com.project.neardoc.utils

import com.project.neardoc.R

enum class NavigationType(
    val id: Int,
    val uriString: String,
    val titleResourceId: Int
) {
    GOOGLE(1, "google.navigation:q=", R.string.navtype_google),
    WAZE(2, "waze://?navigate=yes&q=", R.string.navtype_waze)
}
