package com.project.neardoc.model.localstoragemodels

import androidx.room.*

@Entity(tableName = "doc_practice_phone_list", foreignKeys = [ForeignKey(entity = DocPractice::class, parentColumns = ["id"], childColumns = ["practice_id"])],
    indices = [Index("practice_id")])
data class DocPracticePhoneList(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,
    @ColumnInfo(name = "practice_id")
    var practiceId: String,
    @ColumnInfo(name = "number")
    var phone: String,
    @ColumnInfo(name = "type")
    var type: String
)