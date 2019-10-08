package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StreetAddress(
    @SerializedName("city")
    @Expose
    var city: String,
    @SerializedName("lat")
    @Expose
    var lat: Double,
    @SerializedName("lon")
    @Expose
    var lon: Double,
    @SerializedName("slug")
    @Expose
    var slug: String,
    @SerializedName("state")
    @Expose
    var state: String,
    @SerializedName("state_long")
    @Expose
    var stateFullName: String,
    @SerializedName("street")
    @Expose
    var street: String,
    @SerializedName("street2")
    @Expose
    var street2: String,
    @SerializedName("zip")
    @Expose
    var zip: String
): Parcelable