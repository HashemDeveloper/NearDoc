package com.project.neardoc.model.insuranceplanproviders

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InsuranceData(
    @SerializedName("uid")
    @Expose
    var uid: String,
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("plans")
    @Expose
    var plans: List<Plan>
): Parcelable