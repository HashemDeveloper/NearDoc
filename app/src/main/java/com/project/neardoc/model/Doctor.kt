package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Doctor(
    @SerializedName("practices")
    @Expose
    var practiceList: List<Practice>,
    @SerializedName("educations")
    @Expose
    var educationList: List<Education>,
    @SerializedName("profile")
    @Expose
    var profile: Profile,
    @SerializedName("ratings")
    @Expose
    var ratingList: List<Rating>,
    @SerializedName("insurances")
    @Expose
    var insuranceList: List<Insurance>,
    @SerializedName("specialties")
    @Expose
    var specialityList: List<Specialty>,
    @SerializedName("hospital_affiliations")
    @Expose
    var hospitalAffiliation: HospitalAffiliation,
    @SerializedName("group_affiliations")
    @Expose
    var groupAffiliation: GroupAffiliation,
    @SerializedName("claims")
    @Expose
    var claimList: List<Claim>,
    @SerializedName("licenses")
    @Expose
    var licenseList: List<License>,
    @SerializedName("uid")
    @Expose
    var uid: String,
    @SerializedName("npi")
    @Expose
    var npi: String
): Parcelable