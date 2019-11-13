package com.project.neardoc.model.insuranceplanproviders

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InsuranceMeta(
    @SerializedName("data_type")
    @Expose
    var dataType: String,
    @SerializedName("item_type")
    @Expose
    var itemType: String,
    @SerializedName("total")
    @Expose
    var total: Int,
    @SerializedName("count")
    @Expose
    var count: Int,
    @SerializedName("skip")
    @Expose
    var skip: Int,
    @SerializedName("limit")
    @Expose
    var limit: Int,
    @SerializedName("fields_request")
    @Expose
    var fieldRequested: String,
    @SerializedName("ignored_query_parameters")
    @Expose
    var ignoreQueryParams: List<String>

): Parcelable