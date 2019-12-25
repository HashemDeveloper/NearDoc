package com.project.neardoc.model.localstoragemodels

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.project.neardoc.model.Language

@Entity(tableName = "doc_profile",
    foreignKeys = [ForeignKey(entity = Doc::class, parentColumns = ["doc_parent_id"], childColumns = ["doc_id"], onDelete = CASCADE, onUpdate = CASCADE)],
    indices = [Index("doc_id")])
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
)