package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UsersRes(
    @SerializedName("email")
    @Expose
    var email: String,
    @SerializedName("fullName")
    @Expose
    var fullName: String,
    @SerializedName("image")
    @Expose
    var image: String,
    @SerializedName("isEmailVerified")
    @Expose
    var isEmailVerified: Boolean,
    @SerializedName("loginProvider")
    @Expose
    var loginProvider: String,
    @SerializedName("username")
    @Expose
    var username: String
): Parcelable