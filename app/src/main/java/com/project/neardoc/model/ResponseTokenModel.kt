package com.project.neardoc.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResponseTokenModel(
    @SerializedName("idToken")
    @Expose
    var idToken: String,
    @SerializedName("refreshToken")
    @Expose
    var refreshToken: String,
    @SerializedName("expiresIn")
    @Expose
    var expiresIn: String)