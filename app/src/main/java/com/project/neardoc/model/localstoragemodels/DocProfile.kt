package com.project.neardoc.model.localstoragemodels

import android.os.Parcelable
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.project.neardoc.model.Language
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "doc_profile")
data class DocProfile(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var docProfileId: String,
    @ColumnInfo(name = "doc_id")
    var docId: String,
    @ColumnInfo(name = "user_email")
    var userEmail: String,
    @ColumnInfo(name = "first_name")
    var firstName: String,
    @ColumnInfo(name = "last_name")
    var lastName: String,
    @ColumnInfo(name = "slug")
    var slug: String,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "image_url")
    var imageUrl: String,
    @ColumnInfo(name = "gender")
    var gender: String,
    @ColumnInfo(name = "bio")
    var bio: String,
    @ColumnInfo(name = "unique_uid")
    var uniqueUid: String
) : Parcelable