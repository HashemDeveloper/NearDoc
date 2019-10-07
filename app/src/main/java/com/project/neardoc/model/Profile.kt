package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Profile(
    @SerializedName("first_name")
    @Expose
    var firstName: String,
    @SerializedName("last_name")
    @Expose
    var lastName: String,
    @SerializedName("slug")
    @Expose
    var slug: String,
    @SerializedName("title")
    @Expose
    var title: String,
    @SerializedName("image_url")
    @Expose
    var imageUrl: String,
    @SerializedName("gender")
    @Expose
    var gender: String,
    @SerializedName("languages")
    @Expose
    var listOfLanguage: List<Language>,
    @SerializedName("bio")
    @Expose
    var bio: String
): Parcelable