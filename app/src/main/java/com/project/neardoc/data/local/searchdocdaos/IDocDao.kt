package com.project.neardoc.data.local.searchdocdaos

import androidx.paging.DataSource
import androidx.room.*
import com.project.neardoc.model.localstoragemodels.Doc
import com.project.neardoc.model.localstoragemodels.DocAndRelations

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
    @Transaction @Query("SELECT * FROM doc_parent INNER JOIN practice ON doc_parent.doc_parent_id= practice.doc_id ORDER BY distance ASC")
    fun getAllDoctorsInformation(): DataSource.Factory<Int, DocAndRelations>
}