package com.project.neardoc.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PracticeDoctor(
    @SerializedName("educations")
    @Expose
    var educationList: List<Education>,
    @SerializedName("profile")
    @Expose
    var profile: Profile,
    @SerializedName("ratings")
    @Expose
    var ratings: List<Rating>,
    @SerializedName("insurances")
    @Expose
    var insurances: List<Insurance>,
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
    var medicareClaimStatistics: Claim,
    @SerializedName("licenses")
    @Expose
    var licenses: List<License>,
    @SerializedName("uid")
    @Expose
    var uid: String,
    @SerializedName("npi")
    @Expose
    var npi: String
): Parcelable