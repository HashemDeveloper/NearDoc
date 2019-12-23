package com.project.neardoc.model.localstoragemodels

import androidx.room.*

@Entity(tableName = "doc_practice_office_hour", foreignKeys = [ForeignKey(entity = DocPractice::class, parentColumns = ["id"], childColumns = ["practice_id"])],
    indices = [Index("practice_id")])
data class DocPracticeOfficeHrList(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,
    @ColumnInfo(name = "practice_id")
    var docPracticeId: String,
    @ColumnInfo(name = "office_hours")
    var officeHours: String
)