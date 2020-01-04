package com.project.neardoc.model

import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "hospital_affiliation")
@Parcelize
data class HospitalAffiliation(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String,
    @ColumnInfo(name = "doc_id")
    var docId: String,
    @ColumnInfo(name = "uid")
    @SerializedName("uid")
    @Expose
    var uid: String,
    @ColumnInfo(name = "name")
    @SerializedName("name")
    @Expose
    var name: String,
    @ColumnInfo(name = "type")
    @SerializedName("type")
    @Expose
    var type: String,
    @ColumnInfo(name = "address")
    @SerializedName("address")
    @Expose
    var address: StreetAddress?,
    @ColumnInfo(name = "phone")
    @SerializedName("phone")
    @Expose
    var phone: Phone?
): Parcelable