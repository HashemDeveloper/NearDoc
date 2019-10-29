package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CurrentUserData(
    @SerializedName("localId")
    @Expose
    var localId: String,
    @SerializedName("email")
    @Expose
    var email: String,
    @SerializedName("passwordHash")
    @Expose
    var hashedPassword: String,
    @SerializedName("emailVerified")
    @Expose
    var isEmailVerified: Boolean,
    @SerializedName("passwordUpdatedAt")
    @Expose
    var passwordUpdateAt: Float,
    @SerializedName("providerUserInfo")
    @Expose
    var providerUserInfo: List<CurrentUserProviderUserInfo>,
    @SerializedName("validSince")
    @Expose
    var idTokenValidSince: String,
    @SerializedName("lastLoginAt")
    @Expose
    var lastLoginAt: String,
    @SerializedName("createdAt")
    @Expose
    var createdAt: String,
    @SerializedName("initialEmail")
    @Expose
    var initalEmail: String,
    @SerializedName("lastRefreshAt")
    @Expose
    var lastRefreshAt: String

): Parcelable