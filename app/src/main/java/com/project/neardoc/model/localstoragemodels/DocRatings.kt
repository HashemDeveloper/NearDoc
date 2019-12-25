package com.project.neardoc.model.localstoragemodels

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "doc_ratings",
    foreignKeys = [ForeignKey(entity = Doc::class, parentColumns = ["doc_parent_id"], childColumns = ["doc_id"], onDelete = CASCADE, onUpdate = CASCADE)],
    indices = [Index("doc_id")])
data class DocRatings(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String,
    @ColumnInfo(name = "doc_id")
    var docId: String,
    @ColumnInfo(name = "active")
    var active: Boolean,
    @ColumnInfo(name = "provider")
    var provider: String,
    @ColumnInfo(name = "provider_uid")
    var providerUid: String,
    @ColumnInfo(name = "provider_url")
    var providerUrl: String,
    @ColumnInfo(name = "rating")
    var rating: Double,
    @ColumnInfo(name = "review_count")
    var reviewCount: Int,
    @ColumnInfo(name = "image_url_small")
    var imageUrlSmall: String,
    @ColumnInfo(name = "image_url_small_2x")
    var imageUrlSmall2x: String,
    @ColumnInfo(name = "image_url_large")
    var imageUrlLarge: String,
    @ColumnInfo(name = "image_url_large_2x")
    var imageUrlLarge2x: String
)