package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Practice(
    @SerializedName("location_slug")
    @Expose
    var locationSlug: String,
    @SerializedName("within_search_area")
    @Expose
    var withinSearchArea: Boolean,
    @SerializedName("distance")
    @Expose
    var distance: Double,
    @SerializedName("lat")
    @Expose
    var lat: Double,
    @SerializedName("lon")
    @Expose
    var lon: Double,
    @SerializedName("uid")
    @Expose
    var uid: String,
    @SerializedName("name")
    @Expose
    var name: String,
    @SerializedName("accepts_new_patients")
    @Expose
    var acceptsNewPatients: Boolean,
    @SerializedName("insurance_uids")
    @Expose
    var insuranceUidList: List<String>,
    @SerializedName("visit_address")
    @Expose
    var visitAddress: VisitAddress,
    @SerializedName("office_hours")
    @Expose
    var officeHours: List<String>,
    @SerializedName("phones")
    @Expose
    var phoneList: List<Phone>,
    @SerializedName("languages")
    @Expose
    var listOfLanguage: List<Language>,
    @SerializedName("image_urls")
    @Expose
    var listOfImages: List<String>,
    @SerializedName("website")
    @Expose
    var website: String,
    @SerializedName("email")
    @Expose
    var email: String,
    @SerializedName("npi")
    @Expose
    var npi: String,
    @SerializedName("slug")
    @Expose
    var slug: String,
    @SerializedName("description")
    @Expose
    var description: String,
    @SerializedName("media")
    @Expose
    var mediaAssetUrls: List<Media>,
    @SerializedName("total_doctors")
    @Expose
    var totalDocInPractice: Int,
    @SerializedName("doctors_pagination_url")
    @Expose
    var paginationUrlForDoc: String,
    @SerializedName("doctors")
    @Expose
    var practiceDoctors: PracticeDoctor

): Parcelable
