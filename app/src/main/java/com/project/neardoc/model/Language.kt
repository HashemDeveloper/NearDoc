package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Language(
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("code")
    @Expose
    var code: String
): Parcelable