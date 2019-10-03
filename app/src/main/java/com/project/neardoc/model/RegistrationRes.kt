package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RegistrationRes(
    @SerializedName("kind")
    @Expose
    var kind: String,
    @SerializedName("idToken")
    @Expose
    var idToken: String,
    @SerializedName("email")
    @Expose
    var email: String,
    @SerializedName("refreshToken")
    @Expose
    var refreshToken: String,
    @SerializedName("expiresIn")
    @Expose
    var expiresIn: String,
    @SerializedName("localId")
    @Expose
    var localId: String? = null
) : Parcelable