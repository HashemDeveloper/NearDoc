package com.project.neardoc.data.local

import androidx.room.*
import com.project.neardoc.model.localstoragemodels.StepCountData

@Dao
interface IStepCountDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStepCountData(stepCountData: StepCountData)
    @Transaction @Query("delete from step_count_data")
    suspend fun clearData()
    @Transaction @Query("select * from step_count_data order by data_inserted_time asc")
    suspend fun getAllData(): List<StepCountData>
}