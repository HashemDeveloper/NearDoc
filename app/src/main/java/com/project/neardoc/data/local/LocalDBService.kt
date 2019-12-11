package com.project.neardoc.data.local

import android.content.Context
import javax.inject.Inject

class LocalDBService @Inject constructor(context: Context){
    private val localDB = NearDocLocalStorage.invoke(context)

    fun getUserInfoDao(): IUserInfoDao {
        return this.localDB.getUserPersonalInfoDao()
    }
}