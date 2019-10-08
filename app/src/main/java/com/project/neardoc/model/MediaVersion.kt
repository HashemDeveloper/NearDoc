package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MediaVersion(
    @SerializedName("thumbnail")
    @Expose
    var thumbnail: String,
    @SerializedName("thumbnail2x")
    @Expose
    var thumbnail2x: String,
    @SerializedName("small")
    @Expose
    var small: String,
    @SerializedName("medium")
    @Expose
    var medium: String,
    @SerializedName("large")
    @Expose
    var large: String,
    @SerializedName("hero")
    @Expose
    var hero: String
): Parcelable