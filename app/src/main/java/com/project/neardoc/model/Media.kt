package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Media(
    @SerializedName("uid")
    @Expose
    var uid: String,
    @SerializedName("status")
    @Expose
    var status: String,
    @SerializedName("url")
    @Expose
    var url: String,
    @SerializedName("tags")
    @Expose
    var tags: List<String>,
    @SerializedName("versions")
    @Expose
    var mediaVersion: MediaVersion
): Parcelable