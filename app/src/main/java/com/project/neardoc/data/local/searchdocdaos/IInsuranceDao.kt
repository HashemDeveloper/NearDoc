package com.project.neardoc.data.local.searchdocdaos

import androidx.paging.DataSource
import androidx.room.*
import com.project.neardoc.model.Insurance
import com.project.neardoc.model.localstoragemodels.DocAndRelations

@Dao
interface IInsuranceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsurance(insurance: List<Insurance>)
    @Update
    suspend fun updateInsurance(insurance: Insurance)
    @Transaction @Query("delete from insurance")
    suspend fun clearInsurance()
    @Transaction @Query("select * from doc_parent where doc_parent_id in(select distinct(doc_id) from insurance)")
    fun getInsuranceList(): DataSource.Factory<Int, DocAndRelations>
}