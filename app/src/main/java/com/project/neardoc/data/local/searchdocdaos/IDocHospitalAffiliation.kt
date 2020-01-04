package com.project.neardoc.data.local.searchdocdaos

import androidx.room.*
import com.project.neardoc.model.HospitalAffiliation

@Dao
interface IDocHospitalAffiliation {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAffiliation(hospitalAffiliation: HospitalAffiliation)
    @Transaction @Query("delete from hospital_affiliation")
    suspend fun clearHospitalAffiliation()
}