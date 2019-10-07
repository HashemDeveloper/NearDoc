package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class KnownConditionRes(
    @SerializedName("meta")
    @Expose
    var meta: Meta,
    @SerializedName("data")
    @Expose
    var dataList: List<KnownConditionData>
): Parcelable