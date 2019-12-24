package com.project.neardoc.model.localstoragemodels

import androidx.room.*

@Entity(tableName = "doc_insurance_uids",
    foreignKeys = [ForeignKey(entity = DocPractice::class, parentColumns = ["id"], childColumns = ["practice_id"])],
    indices = [Index("practice_id")])
data class DocPracticeInsuranceUidList(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long = 0L,
    @ColumnInfo(name = "practice_id")
    var insurnaceUidsId: String,
    @ColumnInfo(name = "insurance_uids")
    var insuranceUid: String
)