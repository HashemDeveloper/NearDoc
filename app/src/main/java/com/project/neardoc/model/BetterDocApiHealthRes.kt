package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BetterDocApiHealthRes(
    @SerializedName("status")
    @Expose
    var status: String,
    @SerializedName("api_version")
    @Expose
    var apiVersion: String,
    @SerializedName("proxy_build_version")
    @Expose
    var proxyBuildVersion: String
): Parcelable