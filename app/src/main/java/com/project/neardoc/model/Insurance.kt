package com.project.neardoc.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "insurance")
data class Insurance(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String,
    @ColumnInfo(name = "doc_id")
    var docId: String,
    @ColumnInfo(name = "insurance_plan")
    @SerializedName("insurance_plan")
    @Expose
    var insurancePlan: InsurancePlan,
    @ColumnInfo(name = "insurance_provider")
    @SerializedName("insurance_provider")
    @Expose
    var insuranceProvider: InsuranceProvider
): Parcelable