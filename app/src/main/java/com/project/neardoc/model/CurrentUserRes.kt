package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CurrentUserRes(
    @SerializedName("kind")
    @Expose
    var kind: String,
    @SerializedName("users")
    @Expose
    var users: List<CurrentUserData>
): Parcelable
