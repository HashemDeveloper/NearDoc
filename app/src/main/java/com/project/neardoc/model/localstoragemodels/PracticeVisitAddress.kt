package com.project.neardoc.model.localstoragemodels

import androidx.room.*

@Entity(tableName = "practice_visit_address",
    foreignKeys = [ForeignKey(entity = DocPractice::class, parentColumns = ["id"], childColumns = ["visit_address_practice_id"])],
    indices = [Index("visit_address_practice_id")])
data class PracticeVisitAddress(
    @PrimaryKey
    @ColumnInfo(name = "visit_address_practice_id")
    var practiceVisitAddressId: String,
    @ColumnInfo(name = "city")
    var city: String,
    @ColumnInfo(name = "lat")
    var lat: Double,
    @ColumnInfo(name = "lon")
    var lon: Double,
    @ColumnInfo(name = "state")
    var state: String,
    @ColumnInfo(name = "state_long")
    var stateLong: String,
    @ColumnInfo(name = "street")
    var street: String,
    @ColumnInfo(name = "street2")
    var streetTwo: String,
    @ColumnInfo(name = "zip")
    var zipCode: String
)