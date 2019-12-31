package com.project.neardoc.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "doc_speciality")
data class Specialty(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String,
    @ColumnInfo(name = "doc_id")
    var docId: String,
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