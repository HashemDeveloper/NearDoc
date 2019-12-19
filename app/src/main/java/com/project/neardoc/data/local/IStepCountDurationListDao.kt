package com.project.neardoc.data.local

import androidx.room.*
import com.project.neardoc.model.localstoragemodels.StepCountDurationList

@Dao
interface IStepCountDurationListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDurationList(stepCountDurationList: List<StepCountDurationList>)
    @Transaction @Query("delete from duration_list")
    suspend fun clearDurationList()
    @Transaction @Query("select * from duration_list order by time asc")
    suspend fun getAllDurationList(): List<StepCountDurationList>
    @Transaction @Query("select * from duration_list order by time asc limit 1")
    suspend fun getFirstDuration(): StepCountDurationList
    @Transaction @Query("select * from duration_list order by time desc limit 1")
    suspend fun getLastDuration(): StepCountDurationList
}