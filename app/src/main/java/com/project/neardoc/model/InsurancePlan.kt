package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InsurancePlan(
    @SerializedName("uid")
    @Expose
    var uid: String,
    @SerializedName("category")
    @Expose
    var category: List<String>
): Parcelable