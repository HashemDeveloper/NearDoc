package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VisitAddress(
    @SerializedName("city")
    @Expose
    var city: String,
    @SerializedName("lat")
    @Expose
    var lat: Double,
    @SerializedName("lon")
    @Expose
    var lon: Double,
    @SerializedName("state")
    @Expose
    var state: String,
    @SerializedName("state_long")
    @Expose
    var stateLong: String,
    @SerializedName("street")
    @Expose
    var street: String,
    @SerializedName("street2")
    @Expose
    var streetTwo: String,
    @SerializedName("zip")
    @Expose
    var zipCode: String
): Parcelable