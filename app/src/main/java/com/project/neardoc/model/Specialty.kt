package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Specialty(
    @SerializedName("uid")
    @Expose
    var uid: String,
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("description")
    @Expose
    var description: String,
    @SerializedName("category")
    @Expose
    var category: String,
    @SerializedName("actor")
    @Expose
    var actor: String,
    @SerializedName("actors")
    @Expose
    var actors: String
): Parcelable