package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FirebaseProviderUserInfo(
    @SerializedName("providerId")
    @Expose
    var providerId: String,
    @SerializedName("federatedId")
    @Expose
    var federatedId: String
): Parcelable