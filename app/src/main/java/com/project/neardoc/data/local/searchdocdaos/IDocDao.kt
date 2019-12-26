package com.project.neardoc.data.local.searchdocdaos

import androidx.room.*
import com.project.neardoc.model.localstoragemodels.Doc

@Dao
interface IDocDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctors(doc: Doc)
    @Update
    suspend fun updateDoctors(doc: Doc)
    @Transaction @Query("delete from doc_parent")
    suspend fun clearDocList()
    @Transaction @Query("select * from doc_parent where user_email==:userEmail")
    suspend fun getAllDoctorsByUserEmail(userEmail: String): List<Doc>
}