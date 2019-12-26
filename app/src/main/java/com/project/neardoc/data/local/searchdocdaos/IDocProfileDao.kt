package com.project.neardoc.data.local.searchdocdaos

import androidx.room.*
import com.project.neardoc.model.localstoragemodels.DocAndRelations
import com.project.neardoc.model.localstoragemodels.DocProfile

@Dao
interface IDocProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocProfile(listOfDocProfile: List<DocProfile>)
    @Update
    suspend fun updateDocProfile(listOfDocProfile: List<DocProfile>)
    @Transaction @Query("delete from doc_profile")
    suspend fun clearDocProfile()
    @Transaction @Query("select * from doc_profile where user_email==:userEmail")
    suspend fun getDocProfileByUserEmail(userEmail: String): List<DocProfile>
    @Transaction @Query("select * from doc_parent where doc_parent_id in(select distinct(doc_id) from doc_profile)")
    suspend fun getDoctorsProfile(): List<DocAndRelations>
}