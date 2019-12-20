package com.project.neardoc.model.localstoragemodels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_count_data")
data class StepCountData(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    @ColumnInfo(name = "data_inserted_time")
    val time: Long,
    @ColumnInfo(name = "calories_burned_result")
    val caloriesBurnedResult: Int
)