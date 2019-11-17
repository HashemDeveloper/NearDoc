package com.project.neardoc.model.insuranceplanproviders

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InsuranceAndPlans(
    @SerializedName("meta")
    @Expose
    var meta: InsuranceMeta,
    @SerializedName("data")
    @Expose
    var dataList: List<InsuranceData>
): Parcelable