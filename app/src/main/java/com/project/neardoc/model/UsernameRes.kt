package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UsernameRes(
    @SerializedName("username")
    var username: String
): Parcelable