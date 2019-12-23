package com.project.neardoc.data.local.searchdocdaos

import androidx.room.*
import com.project.neardoc.model.localstoragemodels.DocPractice

@Dao
interface IDocPracticeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocPractice(docPractice: DocPractice)
    @Transaction @Query("delete from practice")
    suspend fun clearDocPractice()
}