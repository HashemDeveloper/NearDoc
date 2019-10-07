package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Phone(
    @SerializedName("number")
    @Expose
    var phone: String,
    @SerializedName("type")
    @Expose
    var type: String
): Parcelable