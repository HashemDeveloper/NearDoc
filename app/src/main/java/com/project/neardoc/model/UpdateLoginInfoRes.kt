package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UpdateLoginInfoRes(
    @SerializedName("localId")
    @Expose
    var localId: String,
    @SerializedName("email")
    @Expose
    var email: String,
    @SerializedName("passwordHash")
    @Expose
    var hashedPassword: String,
    @SerializedName("providerUserInfo")
    @Expose
    var providerUserInfo: List<FirebaseProviderUserInfo>,
    @SerializedName("idToken")
    @Expose
    var idToken: String,
    @SerializedName("refreshToken")
    @Expose
    var refreshToken: String,
    @SerializedName("expiresIn")
    @Expose
    var expireIn: String
): Parcelable