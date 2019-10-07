package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Claim(
    @SerializedName("hcpcs")
    @Expose
    var hcpcs: String, // healthcare common procedure coding system
    @SerializedName("hcpcs_description")
    @Expose
    var hcpcsDescription: String,
    @SerializedName("service_cnt")
    @Expose
    var serviceCount: Int,
    @SerializedName("bene_uniq_cnt")
    @Expose
    var benefitUniqueCount: Int,
    @SerializedName("avg_allowed_amt")
    @Expose
    var averageAllowedAmount: Double,
    @SerializedName("avg_charge_amt")
    @Expose
    var averageChargeAmount: Int,
    @SerializedName("avg_payment_amt")
    @Expose
    var averagePaymentAmount: Double
): Parcelable