package com.project.neardoc.data.local.searchdocdaos

import androidx.room.*
import com.project.neardoc.model.Specialty
import com.project.neardoc.model.localstoragemodels.DocAndRelations

@Dao
interface IDocSpecialityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeciality(specialty: Specialty)
    @Update
    suspend fun updateSpeciality(specialty: Specialty)
    @Transaction @Query("delete from doc_speciality")
    suspend fun clearSpeciality()
    @Transaction @Query("select * from doc_parent where doc_parent_id in(select distinct(doc_id) from doc_speciality)")
    suspend fun getAllDocRatings(): List<DocAndRelations>
}