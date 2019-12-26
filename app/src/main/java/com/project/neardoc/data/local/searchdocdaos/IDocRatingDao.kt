package com.project.neardoc.data.local.searchdocdaos

import androidx.room.*
import com.project.neardoc.model.localstoragemodels.DocAndRelations
import com.project.neardoc.model.localstoragemodels.DocProfile
import com.project.neardoc.model.localstoragemodels.DocRatings

@Dao
interface IDocRatingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoctorRatings(docRating: DocRatings)
    @Update
    suspend fun updateDoctorRatings(docRating: DocRatings)
    @Transaction @Query("delete from doc_ratings")
    suspend fun clearDocRatings()
    @Transaction @Query("select * from doc_parent where doc_parent_id in(select distinct(doc_id) from doc_ratings)")
    suspend fun getAllDocRatings(): List<DocAndRelations>
}