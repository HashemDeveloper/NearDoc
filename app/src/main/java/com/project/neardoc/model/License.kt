package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class License(
    @SerializedName("number")
    @Expose
    var number: String,
    @SerializedName("state")
    @Expose
    var state: String,
    @SerializedName("end_date")
    @Expose
    var endDate: String
): Parcelable