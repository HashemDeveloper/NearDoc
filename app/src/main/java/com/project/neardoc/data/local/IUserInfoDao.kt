package com.project.neardoc.data.local

import androidx.room.*
import com.project.neardoc.model.localstoragemodels.UserPersonalInfo

@Dao
interface IUserInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserInfo(userPersonalInfo: UserPersonalInfo): Long
    @Update
    suspend fun updateUserInfo(userPersonalInfo: UserPersonalInfo)
    @Query("delete from personal_info")
    suspend fun deleteUserPersonalInfo()
    @Query("select * from personal_info where personal_info_id ==:id")
    suspend fun getUserById(id: Long): UserPersonalInfo
    @Query("select * from personal_info where email ==:email")
    suspend fun getUserByEmail(email: String): UserPersonalInfo
}