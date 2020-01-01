package com.project.neardoc.model.localstoragemodels

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.project.neardoc.model.Language
import com.project.neardoc.model.Phone
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "practice")
data class DocPractice(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var practiceId: String,
    @ColumnInfo(name = "doc_id")
    var docId: String,
    @ColumnInfo(name = "location_slug")
    var locationSlug: String,
    @ColumnInfo(name = "within_search_area")
    var withinSearchArea: String,
    @ColumnInfo(name = "distance")
    var distance: Double,
    @ColumnInfo(name = "lat")
    var lat: Double,
    @ColumnInfo(name = "lon")
    var lon: Double,
    @ColumnInfo(name = "uid")
    var uid: String,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "accepts_new_patients")
    var acceptsNewPatients: String,
    @ColumnInfo(name = "website")
    var website: String,
    @ColumnInfo(name = "doc_email")
    var email: String,
    @ColumnInfo(name = "npi")
    var npi: String,
    @ColumnInfo(name = "slug")
    var slug: String,
    @ColumnInfo(name = "phone")
    var phoneList: List<Phone>,
    @ColumnInfo(name = "description")
    var description: String,
    @ColumnInfo(name = "total_doctors")
    var totalDocInPractice: Int,
    @ColumnInfo(name = "doctors_pagination_url")
    var paginationUrlForDoc: String
) : Parcelable