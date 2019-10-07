package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Education(
    @SerializedName("school")
    @Expose
    var school: String,
    @SerializedName("degree")
    @Expose
    var degree: String
): Parcelable