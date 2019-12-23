package com.project.neardoc.model.localstoragemodels

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(tableName = "doc_language", foreignKeys = [ForeignKey(entity = DocProfile::class, parentColumns = ["id"], childColumns = ["profile_id"], onDelete = CASCADE)],
    indices = [Index("profile_id")])
data class DocProfileLanguage(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,
    @ColumnInfo(name = "profile_id")
    var profileId: String,
    @ColumnInfo(name = "name")
    var name: String,
    @ColumnInfo(name = "code")
    var code: String
)