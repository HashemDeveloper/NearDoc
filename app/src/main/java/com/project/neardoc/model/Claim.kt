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
    var numberOfOperationPerformed: Int,
    @SerializedName("bene_uniq_cnt")
    @Expose
    var numberOfUniquePatientTreated: Int,
    @SerializedName("avg_allowed_amt")
    @Expose
    var contractedAmountAllowed: Double,
    @SerializedName("avg_charge_amt")
    @Expose
    var amountBilled: Double,
    @SerializedName("avg_payment_amt")
    @Expose
    var amountPaid: Double
): Parcelable