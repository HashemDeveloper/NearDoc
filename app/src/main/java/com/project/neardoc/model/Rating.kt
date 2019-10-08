package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Rating(
    @SerializedName("active")
    @Expose
    var active: Boolean,
    @SerializedName("provider")
    @Expose
    var provider: String,
    @SerializedName("provider_uid")
    @Expose
    var providerUid: String,
    @SerializedName("provider_url")
    @Expose
    var providerUrl: String,
    @SerializedName("rating")
    @Expose
    var rating: Double,
    @SerializedName("review_count")
    @Expose
    var reviewCount: Int,
    @SerializedName("image_url_small")
    @Expose
    var imageUrlSmall: String,
    @SerializedName("image_url_small_2x")
    @Expose
    var imageUrlSmall2x: String,
    @SerializedName("image_url_large")
    @Expose
    var imageUrlLarge: String,
    @SerializedName("image_url_large_2x")
    @Expose
    var imageUrlLarge2x: String
): Parcelable