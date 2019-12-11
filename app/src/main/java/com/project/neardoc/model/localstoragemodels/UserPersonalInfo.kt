package com.project.neardoc.model.localstoragemodels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.project.neardoc.utils.GenderType

@Entity(tableName = "personal_info")
data class UserPersonalInfo(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "personal_info_id")
    val id: Long,
    @ColumnInfo(name = "email")
    val userEmail: String,
    @ColumnInfo(name = "weight")
    val userWeight: Double,
    @ColumnInfo(name = "height")
    val userHeight: Double,
    @ColumnInfo(name = "age")
    val userAge: Int,
    @ColumnInfo(name = "gender")
    val genderType: String)