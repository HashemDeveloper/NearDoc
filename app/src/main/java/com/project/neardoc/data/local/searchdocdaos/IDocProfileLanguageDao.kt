package com.project.neardoc.data.local.searchdocdaos

import androidx.room.*
import com.project.neardoc.model.localstoragemodels.DocProfileAndProfileLanguage
import com.project.neardoc.model.localstoragemodels.DocProfileLanguage

@Dao
interface IDocProfileLanguageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocProfileLanguage(docProfileLanguage: DocProfileLanguage)
    @Transaction @Query("delete from doc_language")
    suspend fun clearLanguage()
    @Transaction @Query("select * from doc_profile where id in(select distinct(profile_id) from doc_language)")
    suspend fun getDocProfileLanguages(): List<DocProfileAndProfileLanguage>
}