package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Insurance(
    @SerializedName("insurance_plan")
    @Expose
    var insurancePlan: InsurancePlan,
    @SerializedName("insurance_provider")
    @Expose
    var insuranceProvider: InsuranceProvider
): Parcelable