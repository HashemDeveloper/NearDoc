package com.project.neardoc.model.localstoragemodels

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "doc_parent")
data class Doc(
    @PrimaryKey
    @ColumnInfo(name = "doc_parent_id")
    var docParentId: String,
    @ColumnInfo(name = "user_email")
    var userEmail: String,
    @ColumnInfo(name = "uid")
    var uid: String
): Parcelable