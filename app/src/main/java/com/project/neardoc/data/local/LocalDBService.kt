package com.project.neardoc.data.local

import android.content.Context
import com.project.neardoc.data.local.searchdocdaos.IDocDao
import com.project.neardoc.data.local.searchdocdaos.IDocProfileDao
import com.project.neardoc.data.local.searchdocdaos.IDocProfileLanguageDao
import com.project.neardoc.data.local.searchdocdaos.IDocRatingDao
import javax.inject.Inject

class LocalDBService @Inject constructor(context: Context){
    private val localDB = NearDocLocalStorage.invoke(context)

    fun getUserInfoDao(): IUserInfoDao {
        return this.localDB.getUserPersonalInfoDao()
    }
    fun getStepCountDurationListDao(): IStepCountDurationListDao {
        return this.localDB.getStepCountDurationListDao()
    }
    fun getStepCountDataDao(): IStepCountDataDao {
        return this.localDB.getStepCountDataDao()
    }
    fun getDocDao(): IDocDao {
        return this.localDB.getDocDao()
    }
    fun getDocRatingDao(): IDocRatingDao {
        return this.localDB.getDocRatingDao()
    }
    fun getDocProfileDao(): IDocProfileDao {
        return this.localDB.getDocProfileDao()
    }
    fun getDocProfileLanguageDao(): IDocProfileLanguageDao {
        return this.localDB.getDocProfileLanguageDao()
    }
}