package com.project.neardoc.model.localstoragemodels

import androidx.room.*

@Entity(tableName = "duration_list")
data class StepCountDurationList(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "email")
    val email: String,
    @ColumnInfo(name = "time")
    val time: Long
)